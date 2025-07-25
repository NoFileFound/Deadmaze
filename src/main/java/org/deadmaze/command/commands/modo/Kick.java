package org.deadmaze.command.commands.modo;

// Imports
import java.util.List;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

@Command(
        name = "kick",
        usage = "[playerName]",
        description = "Disconnects the player from the game.",
        permission = {Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Kick implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String playerName = args.getFirst();
        if(!server.checkIsConnected(playerName)) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidusername"));
            return;
        }

        server.getPlayers().get(playerName).closeConnection();
        CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("kickedplayer", playerName));
        server.sendServerMessage(Application.getTranslationManager().get("kickedplayer_notify", playerName, player.getPlayerName()), false, player);
    }
}