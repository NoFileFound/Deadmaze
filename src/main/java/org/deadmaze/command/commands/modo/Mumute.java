package org.deadmaze.command.commands.modo;

// Imports
import java.util.List;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

@Command(
        name = "mumute",
        usage = "[playerName]",
        description = "Prevents the player from talking without them knowing, just for the duration of the connection.",
        permission = {Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Mumute implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String playerName = args.getFirst();
        Client playerClient = server.getPlayers().get(playerName);
        if(playerClient != null) {
            playerClient.isMumuted = !playerClient.isMumuted;
            if(playerClient.isMumuted) {
                CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("mumuted_result", playerName));
                server.sendServerMessage(Application.getTranslationManager().get("mumuted_notify", player.getPlayerName(), playerName), false, player);
            } else {
                CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("unmumuted_result", playerName));
                server.sendServerMessage(Application.getTranslationManager().get("unmumuted_notify", player.getPlayerName(), playerName), false, player);
            }
        } else {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidusername"));
        }
    }
}