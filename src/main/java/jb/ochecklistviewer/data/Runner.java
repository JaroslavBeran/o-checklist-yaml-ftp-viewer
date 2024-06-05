package jb.ochecklistviewer.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;


/**
 * <pre>
 *  # Start status report
 *  #
 *  # Report meta data
 *  # Version: YAML document version.
 *  # Creator: App name (with quotes).
 *  # Created: Report creation timestamp (ISO 8601 date and time).
 *  #
 *  # Runner's data
 *  # Runner.Id: IOF xml person id (with quotes), or, null (eg, if CSV import).
 *  # Runner.Name: Name of runner (with quotes).
 *  # Runner.Org: Organisation that the runner represents (with quotes).
 *  # Runner.Card: Card number, or, null (eg, mechanical punching, runner without card).
 *  # Runner.StartTime: Start time, one of: ISO 8601 date and time, or,
 *  # time only (if no date, eg CSV import), or, null (eg, free start time).
 *  # Runner.ClassName: Class in which the runner participates (with quotes).
 *  #
 *  # Status of runner
 *  # Runner.StartStatus: "Started OK", "DNS", or, "Late start" (without quotes).
 *  # Runner.NewCard: New/changed card number (skipped if null/empty).
 *  # Runner.Comment: Free text (with quotes, skipped if null/empty).
 *  #
 *  # Timestamps for status changes (ISO 8601 date and time), set ChangeLog to null if no changes.
 *  # ChangeLog.DNS: Timestamp for DNS (skipped if null/empty, ie, if Started OK).
 *  # ChangeLog.LateStart: Timestamp for Late start (ie, DNS => Late start, skipped if null/empty).
 *  # ChangeLog.NewCard: Timestamp for entering new card number (skipped if no card change).
 *  # ChangeLog.Comment: Timestamp for last edit of comment (skipped if no comment).
 *
 *  Version: 1.1
 *  Creator: "O Checklist v3.3.3"
 *  Created: 2023-01-28T12:34:56+01:00
 *
 *  Data:
 *  # Started OK, no changes.
 *  - Runner:
 *  StartStatus: Started OK
 *  Id: "PRA1234"
 *  Name: "Jana Nováková"
 *  Org: "Praha"
 *  Card: 123456
 *  StartTime: 2023-01-28T12:31:00+01:00
 *  ClassName: "D21"
 *  ChangeLog: null
 *
 *  # DNS.
 *  - Runner:
 *  StartStatus: DNS
 *  Id: "BRN2345"
 *  Name: "Marie Svobodová"
 *  Org: "Brno"
 *  Card: 234567
 *  StartTime: 2023-01-28T12:33:00+01:00
 *  ClassName: "D35"
 *  ChangeLog:
 *  DNS: 2023-01-28T12:34:56+01:00
 *
 *  # Late start.
 *  - Runner:
 *  StartStatus: Late start
 *  Id: "OST3456"
 *  Name: "Eva Novotná"
 *  Org: "Ostrava"
 *  Card: 345678
 *  StartTime: 2023-01-28T12:30:00+01:00
 *  ClassName: "D16"
 *  ChangeLog:
 *  LateStart: 2023-01-28T12:37:02+01:00
 *  DNS: 2023-01-28T12:31:22+01:00
 *
 *  # Card change.
 *  - Runner:
 *  NewCard: 987654
 *  StartStatus: Started OK
 *  Id: "PLZ4567"
 *  Name: "Jiří Dvořák"
 *  Org: "Plzeň"
 *  Card: 456789
 *  StartTime: 2023-01-28T12:37:00+01:00
 *  ClassName: "H65"
 *  ChangeLog:
 *  NewCard: 2023-01-28T12:35:34+01:00
 *
 *  # Card change, Late start, and, Comment.
 *  - Runner:
 *  NewCard: 98765
 *  StartStatus: Late start
 *  Id: "LIB5678"
 *  Name: "Jan Černý"
 *  Org: "Liberec"
 *  Card: 567890
 *  StartTime: 2023-01-28T12:37:00+01:00
 *  ClassName: "H21"
 *  Comment: "Sorry that I'm late with the wrong card."
 *  ChangeLog:
 *  NewCard: 2023-01-28T12:40:08+01:00
 *  LateStart: 2023-01-28T12:40:20+01:00
 *  DNS: 2023-01-28T12:38:51+01:00
 *  Comment: 2023-01-28T12:40:08+01:00
 *
 *  # Runner without IOF xml person id and without start date, eg, from CSV import.
 *  - Runner:
 *  StartStatus: DNS
 *  Id: null
 *  Name: "Lukas Kettner"
 *  Org: "OK Kamenice"
 *  Card: 885632
 *  StartTime: 12:28:00
 *  ClassName: "H21"
 *  ChangeLog:
 *  DNS: 2023-01-28T12:39:46+01:00
 *
 *  # Runner without card number and free start time.
 *  - Runner:
 *  NewCard: 887640
 *  StartStatus: Started OK
 *  Id: "SHK0001"
 *  Name: "Karolína Kettner"
 *  Org: "OK Slavia Hradec Králové"
 *  Card: null
 *  StartTime: null
 *  ClassName: "D21"
 *  ChangeLog:
 *  NewCard: 2023-01-28T12:41:06+01:00
 *
 * </pre>
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Runner {

    /**
     * New/changed card number (skipped if null/empty).
     */
    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("NewCard")
    Long newCard;

    /**
     * "Started OK", "DNS", or, "Late start" (without quotes).
     */
    @EqualsAndHashCode.Exclude
    @JsonSerialize(using = StartStatusSerializer.class)
    @JsonDeserialize(using = StartStatusDeserializer.class)
    @JsonProperty("StartStatus")
    StartStatus startStatus;

    /**
     * IOF xml person id (with quotes), or, null (eg, if CSV import).
     */
    @JsonProperty("Id")
    String id;

    @JsonProperty("Bib")
    String bib;

    /**
     * Name of runner (with quotes).
     */
    @JsonProperty("Name")
    String name;

    /**
     * Organisation that the runner represents (with quotes).
     */
    @JsonProperty("Org")
    String org;

    /**
     * Card number, or, null (eg, mechanical punching, runner without card).
     */
    @JsonProperty("Card")
    Long card;

    /**
     * Start time, one of: ISO 8601 date and time, or, time only (if no date, eg
     * CSV import), or, null (eg, free start `time).
     */
    @JsonProperty("StartTime")
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    OffsetDateTime startTime;

    /**
     * Class in which the runner participates (with quotes).
     */
    @JsonProperty("ClassName")
    String className;

    /**
     * Free text (with quotes, skipped if null/empty).
     */
    @EqualsAndHashCode.Exclude
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("Comment")
    String comment;
}
