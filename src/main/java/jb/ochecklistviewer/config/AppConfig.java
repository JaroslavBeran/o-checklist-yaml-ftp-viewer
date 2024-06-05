package jb.ochecklistviewer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jb.ochecklistviewer.ui.UIConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.EnumMap;
import java.util.List;


@Data
public class AppConfig {

    @JsonProperty("version")
    String version;
    @JsonProperty("appWidth")
    int appWidth = 1000;
    @JsonProperty("appHeight")
    int appHeight = 600;
    @JsonProperty("ftp")
    FtpConfig ftpConfig;
    @JsonProperty("table")
    Table table;

    @AllArgsConstructor
    @Getter
    public enum Order {
        ASC(false),
        DESC(true);


        private final boolean order;
    }


    public record Table(EnumMap<UIConstants.TabColumn, Integer> columnWidth,
                        List<UIConstants.TabColumn> columnPlacement,
                        EnumMap<UIConstants.TabColumn, Order> columnSorting) {
    }
}