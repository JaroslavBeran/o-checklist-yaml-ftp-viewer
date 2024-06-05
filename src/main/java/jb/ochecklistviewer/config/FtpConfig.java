package jb.ochecklistviewer.config;

public record FtpConfig(String server, int port, String user, String pass, String file, int refresh) {


    public static final int FTP_PORT_MIN = 1;
    public static final int FTP_PORT_MAX = 65535;
    public static final int REFRESH_MIN = 1;
    public static final int REFRESH_MAX = 3600;

    public boolean isValid() {
        return !server.isBlank() && (FTP_PORT_MIN <= port && port <= FTP_PORT_MAX) && !user.isBlank() && !pass.isBlank()
                && !file.isBlank() && (REFRESH_MIN <= refresh && refresh <= REFRESH_MAX);
    }
}