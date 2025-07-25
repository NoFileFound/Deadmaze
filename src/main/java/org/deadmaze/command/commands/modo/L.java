package org.deadmaze.command.commands.modo;

// Imports
import static org.deadmaze.utils.Utils.formatUnixTime;
import java.util.List;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;
import org.deadmaze.database.DBUtils;
import org.deadmaze.database.collections.Loginlog;
import org.deadmaze.utils.IPHex;

@Command(
        name = "l",
        usage = "[playerName/IP]",
        description = "Lists the 200 previous connections of the requested nickname or IP address.",
        permission = {Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        aliases = {"loginlog"},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class L implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String playerName = args.getFirst();
        boolean isIP = playerName.matches("^#[0-9A-Fa-f]{2}(\\.[0-9A-Fa-f]{2}){3}$");
        List<Loginlog> info = DBUtils.findConnectionLogs(isIP ? IPHex.decodeIP(playerName) : playerName, isIP);
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("<p align='center'>Connection logs for %s: <BL>%s</BL><br></p>", isIP ? "IP address" : "player", playerName));
        for(Loginlog login : info) {
            String hexIP = IPHex.encodeIP(login.getIpAddress());
            builder.append(String.format("<p align='left'><V>[ %s ]</V> <BL>%s</BL><G> ( <font color = '%s'>%s</font> - %s ) %s - %s</BL><br>", login.getPlayerName(), formatUnixTime(login.getDate(), "MM/dd/yyyy HH:mm"), IPHex.colorIP(hexIP), hexIP, login.getIpCountry(), login.getServiceName(), login.getGameCommunity()));
        }

        if(info.isEmpty()) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidiporname"));
        } else {
            CommandHandler.sendLogMessage(player, 0, builder.toString());
        }
    }
}