package jb.ochecklistviewer.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.builder.HashCodeExclude;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RunnerData {

    @JsonProperty("Runner")
    Runner runner;

    /**
     * <pre>
     * Timestamps for status changes (ISO 8601 date and time), set ChangeLog to null if no changes.
     *   ChangeLog.DNS: Timestamp for DNS (skipped if null/empty, ie, if Started OK).
     *   ChangeLog.LateStart: Timestamp for Late start (ie, DNS => Late start, skipped if null/empty).
     *   ChangeLog.NewCard: Timestamp for entering new card number (skipped if no card change).
     *   ChangeLog.Comment: Timestamp for last edit of comment (skipped if no comment).
     * </pre>
     */
    @EqualsAndHashCode.Exclude
    @JsonProperty("ChangeLog")
    ChangeLog changeLog;

    @EqualsAndHashCode.Exclude
    @HashCodeExclude
    @JsonIgnore
    boolean disappearedFromYaml;

    @EqualsAndHashCode.Exclude
    @HashCodeExclude
    @JsonIgnore
    boolean solved;
}
