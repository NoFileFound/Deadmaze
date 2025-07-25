package org.deadmaze.command.commands.arbitre;

// Imports
import java.util.List;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

@Command(
        name = "commu",
        usage = "[community]",
        description = "Changes your community in-game.",
        permission = { Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR, Command.CommandPermission.ARBITRE, Command.CommandPermission.TRIALMODO, Command.CommandPermission.LUADEV}
)
@SuppressWarnings("unused")
public final class Commu implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String community = args.getFirst();
        if(community.length() != 2) {
            CommandHandler.sendServerMessage(player, "Invalid community name.");
            return;
        }

        player.playerCommunity = community.toUpperCase();
        player.sendEnterRoom(server.getRecommendedRoom(""));
    }
}