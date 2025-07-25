package org.deadmaze.command.commands.arbitre;

// Imports
import java.util.ArrayList;
import java.util.List;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;
import org.deadmaze.utils.IPHex;

@Command(
        name = "lsroom",
        usage = "(roomName)",
        description = "Lists the players present in the room.",
        permission = {Command.CommandPermission.ARBITRE, Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR}
)
@SuppressWarnings("unused")
public final class Lsroom implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String roomName = (!args.isEmpty()) ? args.getFirst() : player.getRoomName();
        if(server.getRooms().get(roomName) != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(Application.getTranslationManager().get("lsroom_results", roomName, server.getRooms().get(roomName).getPlayersCount()));
            List<String> hiddenPlayers = new ArrayList<>();
            for(Client client : server.getRooms().get(roomName).getPlayers().values()) {
                if(client.isHidden) {
                    hiddenPlayers.add(client.getPlayerName());
                } else {
                    builder.append(String.format("<BL>%s / </BL><font color = '%s'>%s</font> <G>(%s)</G><br>", client.getPlayerName(), IPHex.colorIP(IPHex.encodeIP(client.getIpAddress())), IPHex.encodeIP(client.getIpAddress()), client.getCountryName()));
                }
            }
            for(String hiddenPlayer : hiddenPlayers) {
                Client client = server.getPlayers().get(hiddenPlayer);
                builder.append(String.format("<BL>%s / </BL><font color = '%s'>%s</font> <G>(%s)</G> <BL>(invisible)<BL>", client.getPlayerName(), IPHex.colorIP(IPHex.encodeIP(client.getIpAddress())), IPHex.encodeIP(client.getIpAddress()), client.getCountryName()));
            }
            CommandHandler.sendServerMessage(player, builder.toString());
        } else {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("closeroom_roomnotexist", roomName));
        }
    }
}