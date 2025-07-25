package org.deadmaze.command.commands.modo;

// Imports
import com.maxmind.geoip2.record.Continent;
import com.maxmind.geoip2.record.Country;
import java.util.List;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;
import org.deadmaze.libraries.GeoIP;
import org.deadmaze.utils.IPHex;

@Command(
        name = "ip",
        usage = "[playerName]",
        description = "Gives the IP and country of a connected player.",
        permission = {Command.CommandPermission.TRIALMODO, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR},
        requiredArgs = 1
)
@SuppressWarnings("unused")
public final class Ip implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        String playerName = args.getFirst();
        Client playerClient = server.getPlayers().get(playerName);
        if (playerClient == null) {
            CommandHandler.sendServerMessage(player, Application.getTranslationManager().get("invalidusername"));
            return;
        }

        Country countryInfo = GeoIP.getCountry(playerClient.getIpAddress());
        Continent continentInfo = GeoIP.getContinent(playerClient.getIpAddress());

        CommandHandler.sendServerMessage(player, String.format("<BV>%s</BV>'s IP address: %s\n%s - %s (%s) - Community [%s]",
                playerClient.getPlayerName(),
                IPHex.encodeIP(playerClient.getIpAddress()),
                (countryInfo == null) ? "JP" : countryInfo.getIsoCode().toUpperCase(),
                (countryInfo == null) ? "Japan" : countryInfo.getName(),
                (continentInfo == null) ? "Asia" : continentInfo.getName(),
                playerClient.playerCommunity));
    }
}