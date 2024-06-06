package jb.ochecklistviewer.data.validator;

import jb.ochecklistviewer.data.Formatter;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class HhMmSsValidator implements DateTimeValidator {
    @Override
    public boolean isValid(String string) {
        try {
            LocalTime.parse(string, Formatter.hhmmss);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
