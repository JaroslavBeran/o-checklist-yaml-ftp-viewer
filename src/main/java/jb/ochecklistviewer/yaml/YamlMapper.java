package jb.ochecklistviewer.yaml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


class YamlMapper {

    private final ObjectMapper mapper;


    public YamlMapper() {
        var yamlFactory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER) // removes
                // marker
                // characters
                // ---
                .disable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                // .disable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
                // //avoid numbers being quote
                .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR); // list
        // array
        // elements
        // by
        // prefixing
        // hifen

        mapper = new ObjectMapper(yamlFactory);
        mapper.findAndRegisterModules();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(JsonParser.Feature.ALLOW_COMMENTS, JsonParser.Feature.ALLOW_YAML_COMMENTS);
    }


    public <T> T readValue(InputStream is, Class<T> clazz) throws IOException {
        return mapper.readValue(is, clazz);
    }


    public void writeValue(OutputStream os, Object o) throws IOException {
        mapper.writeValue(os, o);
    }

}
