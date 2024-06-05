package jb.ochecklistviewer.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serial;


public class StartStatusDeserializer extends StdDeserializer<StartStatus> {

    @Serial
    private static final long serialVersionUID = -2365497555364213117L;
    private static final Logger log = LoggerFactory.getLogger(StartStatusDeserializer.class);


    public StartStatusDeserializer() {
        this(null);
    }


    protected StartStatusDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public StartStatus deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException {
        String enumTxt = jsonParser.getText();
        if (StartStatus.isValid(enumTxt)) {
            return StartStatus.forValue(enumTxt);
        } else {
            log.warn("Unknown StartStatus enum: {}", enumTxt);
        }
        return null;
    }
}
