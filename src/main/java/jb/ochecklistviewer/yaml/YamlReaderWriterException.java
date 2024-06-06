package jb.ochecklistviewer.yaml;

import java.io.Serial;

public class YamlReaderWriterException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6542792777951368400L;


    public YamlReaderWriterException(String msg, Throwable th) {
        super(msg, th);
    }
}
