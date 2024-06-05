package jb.ochecklistviewer.config;

import java.io.Serial;

public class AppConfigException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5249578256633840794L;

    public AppConfigException(String msg, Throwable t) {
        super(msg, t);
    }
}
