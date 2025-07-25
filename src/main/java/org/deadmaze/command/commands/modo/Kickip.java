package org.deadmaze.command.commands.modo;

// Imports
import java.util.List;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;
import org.deadmaze.utils.IPHex;

@Command(
        name = "kickip",
        usage = "[IP]",
        description = "Disconnects the every player that has the specific ip address.",
        permission = {Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Kickip implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        try {
            server.disconnectIPAddress(IPHex.decodeIP(args.getFirst()), null);
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("kickedip", args.getFirst()));
            server.sendServerMessage(Application.getTranslationManager().get("kickedip_notify", player.getPlayerName(), args.getFirst()), false, player);
        } catch (Exception ignored) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidip"));
        }
    }
}