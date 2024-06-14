package jb.ochecklistviewer.config;

import java.net.MalformedURLException;
import java.net.URL;

public record FtpConfig(String server, int port, String user, String pass, String file, int refresh) {

    static final String FTP_URL_PROTOCOL = "ftp://";

    /**
     * Firstly call {@link #isValid(String, int, String, String, String, int)} to check whether the parameters are suitable for the connection
     */
    public FtpConfig(String server,
                     int port,
                     String user,
                     String pass,
                     String file,
                     int refresh) {
        try {
            URL ftpUrl = new URL(normalizeForURL(server));
            this.server = normalizeForApp(ftpUrl.getHost());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.file = file;
        this.refresh = refresh;
    }

    public static final int FTP_PORT_MIN = 1;
    public static final int FTP_PORT_MAX = 65535;
    public static final int REFRESH_MIN = 1;
    public static final int REFRESH_MAX = 3600;

    public boolean isValid() {
        return isValid(server, port, user, pass, file, refresh);
    }

    public static boolean isValid(String server, int port, String user, String pass, String file, int refresh) {
        if (server.isBlank() ||
                (port < FTP_PORT_MIN || port > FTP_PORT_MAX) ||
                user.isBlank() ||
                pass.isBlank() ||
                file.isBlank() ||
                (refresh < REFRESH_MIN || refresh > REFRESH_MAX)) {
            return false;
        }

        try {
            new URL(normalizeForURL(server));
        } catch (MalformedURLException e) {
            return false;
        }

        return true;
    }

    private static String normalizeForURL(String hostname) {
        return hostname.toLowerCase().startsWith(FTP_URL_PROTOCOL) ? hostname : FTP_URL_PROTOCOL + hostname;
    }

    private static String normalizeForApp(String hostname) {
        return hostname.toLowerCase().startsWith(FTP_URL_PROTOCOL) ? hostname.substring(FTP_URL_PROTOCOL.length()) : hostname;
    }
}