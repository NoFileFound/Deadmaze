package org.deadmaze.command.commands.arbitre;

// Imports
import java.util.List;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

@Command(
        name = "chercher",
        usage = "[Text]",
        description = "Gives the name of the room where a player is located.",
        permission = {Command.CommandPermission.ARBITRE, Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        aliases = {"search", "find"},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Chercher implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String text = args.getFirst();

        boolean check = false;
        for(Client client : server.getPlayers().values()) {
            if(client.getPlayerName().contains(text)) {
                check = true;
                CommandHandler.sendServerMessage(player, String.format("<BV>%s</BV> -> %s<br>", client.getPlayerName(), client.getRoomName()));
            }
        }

        if(!check) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("chercher_noresults"));
        }
    }
}