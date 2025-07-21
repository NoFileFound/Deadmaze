package org.deadmaze;

// Imports
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

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


    }

    /**
     * Removes a player from the current room.
     * @param player The player to remove.
     */
    public void removePlayer(Client player) {
        if(!this.players.containsValue(player)) return;

        this.players.remove(player.getPlayerName());
        player.sendLoadRemoveScene();
    }

    public int getPlayersCount() {
        return this.players.size();
    }
}