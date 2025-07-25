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

@Command(
        name = "imute",
        usage = "[playerName] [hours] [reason]",
        description = "Prevents the player from speaking for the set duration, unmarked message in the room.",
        permission = {Command.CommandPermission.ARBITRE, Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 3
)
@SuppressWarnings("unused")
public final class Imute implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String playerName = args.getFirst();
        Sanction mySanction = server.getLatestSanction(playerName, "mutejeu");
        if (mySanction != null && mySanction.getState().equals("Active")) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("alreadymuted", playerName));
            return;
        } else {
            mySanction = server.getLatestSanction(playerName, "mutedef");
            if (mySanction != null && mySanction.getState().equals("Active")) {
                CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("alreadymuted", playerName));
                return;
            }
        }

        int muteHours = 0;
        try {
            muteHours = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidnum"));
        }

        Client playerClient = server.getPlayers().get(playerName);
        String reason = String.join(" ", args.subList(2, args.size()));
        if(server.getPlayerAccount(playerName) != null) {
            mySanction = new Sanction(playerName, (playerClient != null) ? IPHex.encodeIP(playerClient.getIpAddress()) : "offline", "mutejeu", player.getPlayerName(), reason, getUnixTime() + (muteHours * 3600L));
            mySanction.save();

            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("playermuted", playerName));
            if(playerClient != null) {
                server.sendServerMessage(Application.getTranslationManager().get("mutenotify", player.getPlayerName(), playerName, muteHours, reason), false, player);
            } else {
                server.sendServerMessage(Application.getTranslationManager().get("mutenotify_offline", player.getPlayerName(), playerName, muteHours, reason), false, player);
            }
        } else {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidusername"));
        }
    }
}