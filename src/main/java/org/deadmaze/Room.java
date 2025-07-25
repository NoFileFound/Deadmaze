package org.deadmaze;

// Imports
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashMap;
import lombok.Getter;
import org.deadmaze.packets.SendPacket;

// Packets
import org.deadmaze.packets.send.chat.C_ChatMessage;

public final class Room {
    private final Server server;
    @Getter private final String roomName;
    @Getter private final String roomCommunity;
    @Getter private final String roomCreator;
    @Getter private final Object2ObjectMap<String, Client> players;

    /**
     * Creates a new room.
     * @param server The server instance.
     * @param roomName The room name.
     * @param roomCreator The room author.
     */
    public Room(Server server, String roomName, String roomCreator) {
        this.server = server;
        this.roomName = roomName;
        this.roomCommunity = (roomName.startsWith("*") ? "int" : roomName.substring(0, roomName.indexOf('-')));
        this.roomCreator = roomCreator;
        this.players = new Object2ObjectOpenHashMap<>();
    }

    /**
     * Adds a player to current room.
     * @param player The player to add.
     */
    public void addPlayer(Client player) {
        player.setRoom(this);
        this.players.put(player.getPlayerName(), player);
        player.sendLoadScene();

        /// TODO: FINISH
    }

    /**
     * Removes a player from the current room.
     * @param player The player to remove.
     */
    public void removePlayer(Client player) {
        if(!this.players.containsValue(player)) return;

        this.players.remove(player.getPlayerName());
        player.sendLoadRemoveScene();

        /// TODO: FINISH
    }

    /**
     * Gets the player count in the room.
     * @return The player count.
     */
    public int getPlayersCount() {
        return this.players.size();
    }

    /**
     * Broadcasts a packet for everyone in the room.
     * @param packet The packet to send.
     */
    public void sendAll(SendPacket packet) {
        for (Client player : new HashMap<>(this.players).values()) {
            player.sendPacket(packet);
        }
    }

    /**
     * Broadcasts a packet for everyone in the room except the given player.
     * @param senderPlayer The given player.
     * @param packet The packet to send.
     */
    public void sendAllOthers(Client senderPlayer, SendPacket packet) {
        for (Client player : new HashMap<>(this.players).values()) {
            if (!player.equals(senderPlayer)) {
                player.sendPacket(packet);
            }
        }
    }

    /**
     * Sends a chat message to everyone in the current room.
     * @param playerName The player name.
     * @param message The message.
     * @param isOnly Send message only to himself.
     */
    public void sendChatMessage(String playerName, String message, boolean isOnly) {
        SendPacket packet = new C_ChatMessage(playerName, message);
        if (isOnly) {
            Client player = this.players.get(playerName);
            if (player != null) {
                player.sendPacket(packet);
            }

        } else {
            this.players.values().forEach(player -> player.sendPacket(packet));
        }
    }
}