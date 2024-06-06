package jb.ochecklistviewer.data.validator;

import jb.ochecklistviewer.data.Formatter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class IsoDateTimeValidator implements DateTimeValidator {
    @Override
    public boolean isValid(String string) {
        try {
            OffsetDateTime.parse(string, Formatter.DATE_TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
