package jb.ochecklistviewer.data;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.*;


public final class Formatter {

    public static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();
    public static final ZoneOffset SYSTEM_ZONE_OFFSET = OffsetDateTime.now(SYSTEM_ZONE_ID).getOffset();
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static DateTimeFormatter hhmm = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2).appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2).toFormatter();

    public static DateTimeFormatter hhmmss = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2).appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2).appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2).toFormatter();


    private static String formatDateTimeHHMM(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.atZoneSameInstant(SYSTEM_ZONE_ID).toLocalDateTime().format(hhmm) : "";
    }


    private static String formatDateTimeHHMMSS(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.atZoneSameInstant(SYSTEM_ZONE_ID).toLocalDateTime().format(hhmmss) : "";
    }


    public static String formatId(RunnerData runnerData) {
        String id = runnerData.getRunner().getId();
        return id != null ? id : "";
    }


    public static String formatName(RunnerData runnerData) {
        return runnerData.getRunner().getName();
    }


    public static String formatOrg(RunnerData runnerData) {
        return runnerData.getRunner().getOrg();
    }


    public static String formatCard(RunnerData runnerData) {
        Long card = runnerData.getRunner().getCard();
        return card != null ? card.toString() : "";
    }


    public static String formatStartTime(RunnerData runnerData) {
        return formatDateTimeHHMMSS(runnerData.getRunner().getStartTime());
    }


    public static String formatClassName(RunnerData runnerData) {
        return runnerData.getRunner().getClassName();
    }


    public static String formatStartStatus(RunnerData runnerData) {
        return runnerData.getRunner().getStartStatus().getLabel();
    }


    public static String formatNewCard(RunnerData runnerData) {
        Long newCard = runnerData.getRunner().getNewCard();
        return newCard != null ? newCard.toString() : "";
    }


    public static String formatNewCardChanged(RunnerData runnerData) {
        var changeLog = runnerData.getChangeLog();
        return changeLog != null ? formatDateTimeHHMMSS(changeLog.getNewCard()) : "";
    }


    public static String formatDnsChanged(RunnerData runnerData) {
        var changeLog = runnerData.getChangeLog();
        return changeLog != null ? formatDateTimeHHMMSS(changeLog.getDns()) : "";
    }


    public static String formatLateStart(RunnerData runnerData) {
        var changeLog = runnerData.getChangeLog();
        return changeLog != null ? formatDateTimeHHMMSS(changeLog.getLateStart()) : "";
    }


    public static String formatComment(RunnerData runnerData) {
        String comment = runnerData.getRunner().getComment();
        return comment != null ? comment : "";
    }


    public static String formatOffsetDateTime(OffsetDateTime offsetDateTime) {
        return DATE_TIME_FORMATTER.format(offsetDateTime);
    }
}
