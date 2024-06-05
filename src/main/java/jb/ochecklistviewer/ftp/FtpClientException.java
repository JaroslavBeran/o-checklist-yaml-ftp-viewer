package jb.ochecklistviewer.ftp;

import java.io.Serial;

public class FtpClientException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3644745642098569341L;


    public FtpClientException(String msg) {
        super(msg);
    }


    public FtpClientException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
