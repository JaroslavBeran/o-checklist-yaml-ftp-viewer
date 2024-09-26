package jb.ochecklistviewer.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.List;


@lombok.Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChecklistReport {

    @JsonProperty("Version")
    String version; // YAML document version

    @JsonProperty("Creator")
    String creator; // App name (with quotes)

    @JsonProperty("Created")
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    OffsetDateTime created; // Report creation timestamp (ISO 8601 date and time)

    @JsonProperty("Event")
    String event;

    @JsonProperty("Data")
    List<RunnerData> runners;
}
