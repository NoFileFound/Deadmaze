package org.deadmaze.command.commands.modo;

// Imports
import java.util.List;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

@Command(
        name = "banhack",
        usage = "[playerName]",
        description = "Special banning order for cheaters.",
        permission = {Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Banhack implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        player.getParseModopwetInstance().sendBanHack(args.getFirst(), false);
    }
}