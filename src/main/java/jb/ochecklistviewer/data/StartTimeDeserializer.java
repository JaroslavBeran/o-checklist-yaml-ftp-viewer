package jb.ochecklistviewer.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import jb.ochecklistviewer.data.validator.DateTimeValidator;
import jb.ochecklistviewer.data.validator.HhMmSsValidator;
import jb.ochecklistviewer.data.validator.IsoDateTimeValidator;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;


public class StartTimeDeserializer extends StdDeserializer<OffsetDateTime> {

    private static final DateTimeValidator HH_MM_SS_VALIDATOR = new HhMmSsValidator();
    private static final DateTimeValidator ISO_DATE_TIME_VALIDATOR = new IsoDateTimeValidator();

    protected StartTimeDeserializer() {
        this(null);
    }


    protected StartTimeDeserializer(Class vc) {
        super(vc);
    }


    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String startTimeStr = p.getText();

        if (HH_MM_SS_VALIDATOR.isValid(startTimeStr)) {
            LocalTime localTime = LocalTime.parse(startTimeStr, Formatter.hhmmss);
            LocalDate localDateNow = LocalDate.now(Formatter.SYSTEM_ZONE_ID);
            return OffsetDateTime.of(localDateNow, localTime, Formatter.SYSTEM_ZONE_OFFSET);
        }

        if (ISO_DATE_TIME_VALIDATOR.isValid(startTimeStr)) {
            return OffsetDateTime.parse(startTimeStr, Formatter.DATE_TIME_FORMATTER);
        }

        throw new IllegalArgumentException("Cannot read date/time from YAML: " + startTimeStr);
    }
}
