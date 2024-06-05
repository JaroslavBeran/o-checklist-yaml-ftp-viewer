package jb.ochecklistviewer.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serial;
import java.time.OffsetDateTime;


public class OffsetDateTimeSerializer extends StdSerializer<OffsetDateTime> {

    @Serial
    private static final long serialVersionUID = 8385263203673791141L;


    protected OffsetDateTimeSerializer(Class<OffsetDateTime> clazz) {
        super(clazz);
    }


    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // 2024-05-01T15:07:14+02:00
        // 2024-05-01T13:07:14Z
        gen.writeString(Formatter.formatOffsetDateTime(value));
    }

}
