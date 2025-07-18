package org.deadmaze.packets.recv.login;

// Imports
import java.util.concurrent.TimeUnit;
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.database.DBUtils;
import org.deadmaze.database.collections.Account;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send.login.C_AccountError;

@SuppressWarnings("unused")
public final class S_RegisterAccount implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        try {
            if(client.getServer().createAccountTimer.get(client.getIpAddress()).getRemainingTime() <= 0) {
                data.decryptPacket(Application.getSwfInfo().packet_keys, fingerPrint);
                String nickname = data.readString();
                String password_hash = data.readString();
                String email = data.readString();
                String captcha = data.readString();
                data.readString();
                String swfUrl = data.readString();
                if(!Application.getSwfInfo().swf_url.isEmpty() && !Application.getSwfInfo().swf_url.equals(swfUrl)) {
                    // xor failure.
                    Application.getLogger().info(Application.getTranslationManager().get("fakeswfconnection", client.getIpAddress()));
                    client.closeConnection();
                    return;
                }

                if(!nickname.matches("^(?=^(?:(?!.*_$).)*$)(?=^(?:(?!_{2,}).)*$)[A-Za-z][A-Za-z0-9_]{2,11}$") || nickname.length() > 11 || password_hash.isEmpty()) {
                    // invalid nickname.
                    client.sendPacket(new C_AccountError(4));
                    return;
                }

                if(!captcha.equals(client.registerCaptcha)) {
                    // wrong captcha
                    client.sendPacket(new C_AccountError(7));
                    return;
                }

                Account account = DBUtils.findAccountByNickname(nickname);
                if(account != null) {
                    // account already exist.
                    client.sendPacket(new C_AccountError(3));
                    return;
                }

                if(DBUtils.findAccountsByEmail(email, "").size() > 7) {
                    // too many accounts in an email address.
                    client.sendPacket(new C_AccountError(10));
                    return;
                }

                try {
                    Account instance = new Account(nickname, email, password_hash, client.getIpAddress());
                    instance.save();
                    client.getServer().createAccountTimer.get(client.getIpAddress()).schedule(() -> {}, TimeUnit.HOURS);
                    client.sendLogin(instance, nickname);
                } catch (Exception e) {
                    // internal error
                    client.getServer().createAccountTimer.get(client.getIpAddress()).cancel();
                    client.sendPacket(new C_AccountError(6));
                    throw new RuntimeException(e);
                }
            } else {
                client.sendPacket(new C_AccountError(5));
            }
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
        return 7;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}