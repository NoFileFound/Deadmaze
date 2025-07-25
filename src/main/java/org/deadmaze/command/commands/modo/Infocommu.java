package org.deadmaze.command.commands.modo;

// Imports
import java.util.List;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

@Command(
        name = "infocommu",
        usage = "[community]",
        description = "Gives info about which game modes are being played by mice connected to community ##.",
        permission = {Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Infocommu implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        /// TODO: [Unimplemented] Commands->infocommu
        throw new RuntimeException("[Unimplemented] Commands->infocommu");
    }
}