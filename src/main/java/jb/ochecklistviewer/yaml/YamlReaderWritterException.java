package jb.ochecklistviewer.yaml;

import java.io.Serial;

public class YamlReaderWritterException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6542792777951368400L;


    public YamlReaderWritterException(String msg, Throwable th) {
        super(msg, th);
    }
}
