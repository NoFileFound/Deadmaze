package org.deadmaze.command.commands.modo;

// Imports
import java.util.List;
import java.util.Set;
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
        name = "ipnom",
        usage = "[IP]",
        description = "Gives the accounts a specific IP has connected to.",
        permission = {Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Ipnom implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String ipAddress = args.getFirst();
        if(!ipAddress.matches("^#[0-9A-Fa-f]{2}(\\.[0-9A-Fa-f]{2}){3}$")) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidip"));
            return;
        }

        List<Loginlog> logs = DBUtils.findConnectionLogs(IPHex.decodeIP(ipAddress), true);
        StringBuilder accountList = new StringBuilder(String.format("Logs for the IP address [%s]:", ipAddress));
        Set<String> distinctUsernames = logs.stream().map(Loginlog::getPlayerName).collect(Collectors.toSet());
        for (String playerName : distinctUsernames) {
            if(server.checkIsConnected(playerName)) {
                accountList.append(String.format("<br>%s <G>(online)</G>", playerName));
            } else {
                accountList.append("<br>").append(playerName);
            }
        }
        CommandHandler.sendServerMessage(player, accountList.toString());
    }
}