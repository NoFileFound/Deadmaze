package org.deadmaze.packets.recv.login;

// Imports
import java.util.List;
import java.util.stream.Collectors;
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.database.DBUtils;
import org.deadmaze.database.collections.Account;
import org.deadmaze.libraries.JakartaMail;
import org.deadmaze.libraries.SrcRandom;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send.login.C_AccountError;

@SuppressWarnings("unused")
public final class S_LoginAccount implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        try {
            data = data.decryptIdentification(Application.getSwfInfo().packet_keys, "identification");
            String nickname = data.readString();
            String password = data.readString();
            String swfUrl = data.readString();
            String startRoom = data.readString();
            int authKey = data.readInt();
            int hardModeUnk = data.readShort();
            int loginMethod = data.readByte();
            String twoFactorAuth = data.readString();
            for (int i = 0; i < Application.getSwfInfo().login_keys.size(); i++) {
                authKey ^= Application.getSwfInfo().login_keys.get(i);
            }

            if((!Application.getSwfInfo().swf_url.isEmpty() && !Application.getSwfInfo().swf_url.equals(swfUrl)) || Application.getSwfInfo().authorization_key != authKey || hardModeUnk != 0x12 || loginMethod != 0x00) {
                Application.getLogger().info(String.format("[Connection] The IP address %s tried to login or register with unofficial swf.", client.getIpAddress()));
                client.closeConnection();
                return;
            }

            if(client.loginAttempts > Application.getPropertiesInfo().login_attempts) {
                Application.getLogger().warn(String.format("[DDOS] The ip %s has exceeded the maximum number of login attempts.", client.getIpAddress()));
                client.getServer().getTempBlackList().add(client.getIpAddress());
                client.closeConnection();
                return;
            }

            if(nickname.contains("@") && Application.getPropertiesInfo().allow_email) {
                List<Account> accountList = DBUtils.findAccountsByEmail(nickname, password);
                if(accountList == null || accountList.isEmpty()) {
                    client.loginAttempts++;
                    client.sendPacket(new C_AccountError(2));
                    return;

                } else if(accountList.size() == 1) {
                    nickname = accountList.getFirst().getPlayerName();
                }
                else {
                    client.sendPacket(new C_AccountError(accountList.stream().map(Account::getPlayerName).collect(Collectors.joining("¤"))));
                    return;
                }
            }

            Account account = DBUtils.findAccountByPassword(nickname, password);
            if(account == null) {
                client.loginAttempts++;
                client.sendPacket(new C_AccountError(2));
                return;
            }

            if(client.getServer().checkIsConnected(nickname)) {
                client.loginAttempts++;
                client.sendPacket(new C_AccountError(1));
                return;
            }

            if(!account.getLastIPAddress().equals(client.getIpAddress()) && !account.getStaffRoles().isEmpty() && !client.hasSent2FAEmail) {
                client.hasSent2FAEmail = true;
                client.playerToken2FA = SrcRandom.generateNumberAndLetters(8);
                client.sendPacket(new C_AccountError(15, client.playerToken2FA));
                JakartaMail.sendMessage(account.getEmailAddress(), "Device verification", String.format("Code: %s | IP Address: %s", client.playerToken2FA, client.getIpAddress()));
                return;
            }

            if(client.hasSent2FAEmail && !twoFactorAuth.equals(client.playerToken2FA)) {
                client.loginAttempts++;
                client.sendPacket(new C_AccountError(2));
                if(client.loginAttempts > 5) {
                    client.hasSent2FAEmail = false;
                }
                return;
            }

            client.sendLogin(account, nickname, false);
        } catch (Exception e) {
            // internal error
            client.sendPacket(new C_AccountError(6));
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getC() {
        return 26;
    }

    @Override
    public int getCC() {
        return 8;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}