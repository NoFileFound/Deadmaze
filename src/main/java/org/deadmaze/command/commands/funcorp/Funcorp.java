package org.deadmaze.command.commands.funcorp;

// Imports
import java.util.List;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

@Command(
        name = "funcorp",
        usage = "(help)",
        description = "Enable/disable the funcorp mode, or show the list of funcorp-related commands.",
        permission = {Command.CommandPermission.FUNCORP, Command.CommandPermission.ADMINISTRATOR}
)
@SuppressWarnings("unused")
public final class Funcorp implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        if(!args.isEmpty() && args.getFirst().equals("help")) {
            String message = "FunCorp Commands: <br><br>" +
                    "<J>/changenick</J> <V>[playerName] [newNickname|off]</V> : <BL> Temporarily changes a player's nickname.</BL><br>" +
                    "<J>/changesize</J> <V>[playerNames|*] [size|off]</V> : <BL> Temporarily changes the size (between 0.1x and 5x) of players.</BL><br>" +
                    "<J>/colormouse </J> <V>[playerNames|*] [color|off]</V> : <BL> Temporarily gives a colorized fur.</BL><br>" +
                    "<J>/colornick</J> <V>[playerNames|*] [color|off]</V> : <BL> Temporarily changes the color of player nicknames.</BL><br>" +
                    "<J>/funcorp</J> <G>[help]</G> : <BL> Enable/disable the funcorp mode, or show the list of funcorp-related commands.</BL><br>" +
                    "<J>/linkmice</J> <V>[playerNames|*]</V> <G>[off]</G> : <BL> Temporarily links players.</BL><br>" +
                    "<J>/lsfc</J> : <BL> List of online funcorps.</BL><br>" +
                    "<J>/meep</J> <V>[playerNames|*]</V> <G>[off]</G> : <BL> Give meep to players.</BL><br>" +
                    "<J>/profil</J> <V>[playerPartName]</V> : <BL> Display player's info. (aliases: /profile, /perfil, /profiel)</BL><br>" +
                    "<J>/room*</J> <V>[roomName]</V> : <BL> Allows you to enter into any room. (aliases: /salon*, /sala*)</BL><br>" +
                    "<J>/roomevent</J> <G>[on|off]</G> : <BL> Highlights the current room in the room list.</BL><br>" +
                    "<J>/transformation</J> <V>[playerNames|*]</V> <G>[off]</G> : <BL> Temporarily gives the ability to transform.</BL><br>" +
                    "<J>/tropplein</J> <V>[maxPlayers]</V> : <BL> Setting a limit for the number of players in a room.</BL><br>";
            CommandHandler.sendLogMessage(player, 0, message);
        }
    }
}