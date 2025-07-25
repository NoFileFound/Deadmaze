package org.deadmaze.command.commands.arbitre;

// Imports
import java.util.List;
import org.deadmaze.Client;
import org.deadmaze.Room;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

@Command(
        name = "ls",
        usage = "[text]",
        description = "Lists of existing rooms that have the chosen keyword.",
        permission = {Command.CommandPermission.ARBITRE, Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR}
)
@SuppressWarnings("unused")
public final class Ls implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        if(args.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for(Room room : server.getRooms().values()) {
                result.append("<BL>").append(room.getRoomName()).append("</BL> <G>(").append(room.getRoomCommunity()).append(" / ").append("MainServer").append(") :</G> <V>").append(room.getPlayersCount()).append("</V><br>");
            }
            result.append("<J>Total players:</J> <R>").append(server.getPlayers().size()).append("</R>");
            CommandHandler.sendLogMessage(player, 0, result.toString());
        } else {
            String matching = args.getFirst();
            StringBuilder result = new StringBuilder(String.format("<N>List of rooms matching [%s]:</N><br>", matching));
            int totalPlayers = 0;
            for(Room room : server.getRooms().values()) {
                if(room.getRoomName().contains(matching)) {
                    result.append("<BL>").append(room.getRoomName()).append("</BL> <G>(").append(room.getRoomCommunity()).append(" / ").append("MainServer").append(") :</G> <V>").append(room.getPlayersCount()).append("</V><br>");
                    totalPlayers += room.getPlayersCount();
                }
            }
            result.append("<J>Total players:</J> <R>").append(totalPlayers).append("</R>");
            CommandHandler.sendLogMessage(player, 0, result.toString());
        }
    }
}