package org.deadmaze.command.commands.modo;

// Imports
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;
import org.deadmaze.database.DBUtils;
import org.deadmaze.database.collections.Loginlog;
import org.deadmaze.utils.IPHex;

@Command(
        name = "relation",
        usage = "[playerName]",
        description = "Gives list of related accounts since the last reboot.",
        permission = {Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Relation implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String playerName = args.getFirst();

        List<Loginlog> logs = DBUtils.findConnectionLogs(playerName, false);
        if(!logs.isEmpty()) {
            Map<String, String> uniqueRelations = logs.stream().filter(log -> log.getIpAddress() != null).collect(Collectors.toMap(Loginlog::getPlayerName, Loginlog::getIpAddress, (ip1, ip2) -> ip1));
            StringBuilder message = new StringBuilder(Application.getTranslationManager().get("relation_result", playerName));
            for (Map.Entry<String, String> entry : uniqueRelations.entrySet()) {
                String username = entry.getKey();
                String ip = IPHex.encodeIP(entry.getValue());
                String currentIndicator = (server.checkIsConnected(username) ? "(current IP)" : "");
                message.append(String.format("<br> - <BV>%s</BV> : <font color='%s'>%s</font> ", username, IPHex.colorIP(ip), ip)).append(currentIndicator);
            }
            CommandHandler.sendServerMessage(player, message.toString());
        } else {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("nomip_noresult", playerName));
        }
    }
}