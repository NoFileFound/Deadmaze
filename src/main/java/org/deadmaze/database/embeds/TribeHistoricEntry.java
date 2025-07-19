package org.deadmaze.database.embeds;

// Imports
import dev.morphia.annotations.Embedded;
import lombok.Getter;
import org.deadmaze.utils.Utils;

@Getter
@Embedded
public class TribeHistoricEntry {
    private final int type;
    private final int date;
    private final String information;

    public TribeHistoricEntry(int type, String information) {
        this.type = type;
        this.date = Utils.getTribulleTime();
        this.information = information;
    }
}