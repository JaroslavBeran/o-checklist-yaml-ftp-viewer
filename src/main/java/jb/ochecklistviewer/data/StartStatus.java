package jb.ochecklistviewer.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.io.IOException;
import java.util.stream.Stream;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StartStatus {

    STARTED_OK("Started OK"),
    DNS("DNS"),
    LATE_START("Late start");


    private final String label;


    StartStatus(String label) {
        this.label = label;
    }


    public static StartStatus forValue(String value) throws IOException {
        return Stream.of(StartStatus.values()).filter(e -> e.label.equals(value)).findFirst()
                .orElseThrow(() -> new IOException("Cannot deserialize StartStatus"));
    }


    public static boolean isValid(String value) {
        return Stream.of(StartStatus.values()).anyMatch(e -> e.label.equals(value));
    }
}
