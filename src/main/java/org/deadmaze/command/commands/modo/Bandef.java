package org.deadmaze.command.commands.modo;

// Imports
import java.util.List;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

@Command(
        name = "bandef",
        usage = "[playerName]",
        description = "Special banning order for cheaters permanently.",
        permission = {Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Bandef implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        player.getParseModopwetInstance().sendBanDef(args.getFirst(), String.join(" ", args.subList(1, args.size())), false);
    }
}