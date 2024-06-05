package jb.ochecklistviewer.yaml;

import com.google.common.base.Preconditions;
import jb.ochecklistviewer.data.ChecklistReport;

import java.io.*;


public class YamlReaderWriter {

    private static final YamlMapper mapper = new YamlMapper();


    public static ChecklistReport readReport(File file) {
        Preconditions.checkArgument(file != null, "Input file must not be null");

        try (FileInputStream is = new FileInputStream(file)) {
            return mapper.readValue(is, ChecklistReport.class);
        } catch (Exception e) {
            throw new YamlReaderWritterException("Cannot parse YAML file " + file.getName(), e);
        }
    }


    public static ChecklistReport readReport(InputStream is) {
        Preconditions.checkArgument(is != null, "Input stream must not be null");
        return read(is, ChecklistReport.class);
    }


    public static void writeReport(ChecklistReport report, File toFile) {
        try (FileOutputStream fos = new FileOutputStream(toFile)) {
            mapper.writeValue(fos, report);
        } catch (IOException e) {
            throw new YamlReaderWritterException("Cannot generate or write YAML: " + toFile.getName(), e);
        }
    }


    public static <T> T read(InputStream is, Class<T> clazz) {
        Preconditions.checkArgument(is != null, "Input stream must not be null");
        Preconditions.checkArgument(clazz != null, "Input clazz must not be null");

        try {
            return mapper.readValue(is, clazz);
        } catch (Exception e) {
            throw new YamlReaderWritterException("Cannot parse input stream", e);
        }
    }

}
