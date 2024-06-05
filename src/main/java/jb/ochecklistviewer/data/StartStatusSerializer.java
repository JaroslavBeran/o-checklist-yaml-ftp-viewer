package jb.ochecklistviewer.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serial;


public class StartStatusSerializer extends StdSerializer<StartStatus> {

    @Serial
    private static final long serialVersionUID = -6197861898511159800L;


    protected StartStatusSerializer() {
        this(null);
    }


    protected StartStatusSerializer(Class<StartStatus> t) {
        super(t);
    }


    @Override
    public void serialize(StartStatus value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getLabel());
    }
}
