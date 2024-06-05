package jb.ochecklistviewer.ui;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UIConstants {

    static final int TABLE_COLUMN_DEFAULT_WIDTH;
    static Color TABLE_ROW_SELECTION_BACKGROUND_COLOR;
    static Color TABLE_ROW_ZEBRA_DARK;
    static Color TABLE_ROW_ZEBRA_LIGHT;

    static {
        TABLE_ROW_SELECTION_BACKGROUND_COLOR = new Color(0x00, 0x77, 0xc5);
        // TABLE_ROW_ZEBRA_DARK = new Color(0xd5, 0xd5, 0xd5);
        TABLE_ROW_ZEBRA_DARK = new Color(0xed, 0xed, 0xed);
        // TABLE_ROW_ZEBRA_LIGHT = new Color(0xd5, 0xd5, 0xd5).brighter();
        TABLE_ROW_ZEBRA_LIGHT = new Color(0xfe, 0xfe, 0xfe);

        TABLE_COLUMN_DEFAULT_WIDTH = 75;
    }


    @Getter
    @AllArgsConstructor
    public enum ColumnIdentification {
        ID("ID"),
        NAME("Name"),
        ORG("Org"),
        CARD("Card"),
        START("Start"),
        CLASS("Class"),
        START_STATUS("Start Status"),
        NEW_CARD("New Card"),
        NEW_CARD_CHANGED("New Card changed"),
        DNS("DNS"),
        LATE_START("Late Start"),
        COMMENT("Comment"),
        ;


        private final String label;
    }
}
