package org.deadmaze.modules;

import org.deadmaze.Client;
import org.deadmaze.packets.send._112.C_SetPlayerVitals;
import org.deadmaze.packets.send._116.C_SetThreatLevelInfo;

public class ParseAbilities {
    private final Client client;

    public ParseAbilities(final Client client) {
        this.client = client;
    }


    public void sendInitAbilities() {
        this.client.sendPacket(new C_SetPlayerVitals(this.client.getAccount().getRestPoints(), this.client.getAccount().getRestPointsMax(), this.client.getAccount().getThirstPoints(), this.client.getAccount().getThirstPointsMax(), this.client.getAccount().getFoodPoints(), this.client.getAccount().getFoodPointsMax()));
        this.client.sendPacket(new C_SetThreatLevelInfo(this.client.getAccount().getThreatLevelXp(), this.client.getAccount().getThreatLevelNextXp(), this.client.getAccount().getThreatLevel()));
    }
}