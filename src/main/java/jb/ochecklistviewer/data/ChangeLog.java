package jb.ochecklistviewer.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeLog {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonProperty("NewCard")
    OffsetDateTime newCard;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonProperty("LateStart")
    OffsetDateTime lateStart;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonProperty("DNS")
    OffsetDateTime dns;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonProperty("Comment")
    OffsetDateTime comment;
}
