package org.deadmaze.command.commands.arbitre;

// Imports
import static org.deadmaze.utils.Utils.getUnixTime;
import java.util.List;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;
import org.deadmaze.database.collections.Sanction;
import org.deadmaze.utils.IPHex;

// Packets
import org.deadmaze.packets.send.legacy.C_BanMessageLogin;

@Command(
        name = "iban",
        usage = "[playerName] [hours] [reason]",
        description = "Default ban command, message is not sent in the room.",
        permission = {Command.CommandPermission.ARBITRE, Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 3
)
@SuppressWarnings("unused")
public final class Iban implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String playerName = args.getFirst();
        Sanction mySanction = server.getLatestSanction(playerName, "banjeu");
        if (mySanction != null && mySanction.getState().equals("Active")) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("alreadybanned", playerName));
            return;
        } else {
            mySanction = server.getLatestSanction(playerName, "bandef");
            if (mySanction != null && mySanction.getState().equals("Active")) {
                CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("alreadybanned", playerName));
                return;
            }
        }

        int banHours = 0;
        try {
            banHours = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidnum"));
        }

        Client playerClient = server.getPlayers().get(playerName);
        String reason = String.join(" ", args.subList(2, args.size()));
        if(server.getPlayerAccount(playerName) != null) {
            mySanction = new Sanction(playerName, (playerClient != null) ? IPHex.encodeIP(playerClient.getIpAddress()) : "offline", "banjeu", player.getPlayerName(), reason, getUnixTime() + (banHours * 3600L));
            mySanction.save();

            if(playerClient != null) {
                server.sendServerMessage(Application.getTranslationManager().get("banplayernotify", player.getPlayerName(), playerName, banHours, reason), false, null);
                server.disconnectIPAddress(playerClient.getIpAddress(), player);
                playerClient.sendOldPacket(new C_BanMessageLogin(banHours, reason));
            } else {
                server.sendServerMessage(Application.getTranslationManager().get("banplayernotify_offline", player.getPlayerName(), playerName, banHours, reason), false, null);
            }
        } else {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidusername"));
        }
    }
}