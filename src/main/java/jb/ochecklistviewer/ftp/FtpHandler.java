package jb.ochecklistviewer.ftp;

import jb.ochecklistviewer.config.FtpConfig;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Duration;


public class FtpHandler {

    private static final Logger log = LoggerFactory.getLogger(FtpHandler.class);
    private final static int CONNECTION_TIMEOUT = 13000;
    private FtpConfig ftpConfig;
    private FTPClient ftpApacheClient;
    private final FtpClientListener listener;


    public FtpHandler(FtpClientListener listener) {
        this.listener = listener;
    }


    public synchronized void disconnect() {
        if (ftpApacheClient == null) {
            return;
        }

        if (ftpApacheClient.isConnected()) {
            try {
                ftpApacheClient.logout();
                ftpApacheClient.disconnect();
            } catch (IOException ioe) {
                // do nothing
            }
        }

        ftpApacheClient = null;
        ftpConfig = null;
        listener.publish("Disconnected");
    }


    public synchronized void connect(FtpConfig ftpConfig) {
        this.ftpConfig = ftpConfig;

        ftpApacheClient = new FTPClient();
        FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_L8);
        ftpApacheClient.configure(config);
        ftpApacheClient.setConnectTimeout(CONNECTION_TIMEOUT);
        ftpApacheClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

        try {
            int reply;
            listener.publish("Connecting to %s:%d".formatted(ftpConfig.server(), ftpConfig.port()));
            ftpApacheClient.connect(ftpConfig.server(), ftpConfig.port());

            // After connection attempt, you should check the reply code to
            // verify
            // success.
            reply = ftpApacheClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                disconnect();
                String ftpConectionRefused = "FTP server refused connection.";
                listener.publish(ftpConectionRefused);
                throw new FtpClientException(ftpConectionRefused);
            }

            // Login with USER
            reply = ftpApacheClient.user(ftpConfig.user());
            if (!FTPReply.isPositiveIntermediate(reply)) {
                disconnect();
                var ftpUserError = "FTP server refused USER command.";
                listener.publish(ftpUserError);
                throw new FtpClientException(ftpUserError);
            }

            // ... and with PASS
            reply = ftpApacheClient.pass(ftpConfig.pass());
            if (!FTPReply.isPositiveCompletion(reply)) {
                disconnect();
                var ftpPassError = "FTP server refused PASS command.";
                listener.publish(ftpPassError);
                throw new FtpClientException(ftpPassError);
            }

            // Keep alive
            ftpApacheClient.setControlKeepAliveTimeout(Duration.ofMinutes(1));

            // Set BINARY file type transfer
            if (!ftpApacheClient.setFileType(FTP.BINARY_FILE_TYPE)) {
                disconnect();
                var ftpCannotSetBinary = "Cannot set BINARY mode.";
                throw new FtpClientException(ftpCannotSetBinary);
            }

            // Enable passive mode
            ftpApacheClient.enterLocalPassiveMode();

            listener.publish("Connected");
        } catch (UnknownHostException unknownHostEx) {
            disconnect();
            var ftpUnknownHostEx = "FTP - Unknown host";
            listener.publish(ftpUnknownHostEx);
            throw new FtpClientException(ftpUnknownHostEx);
        } catch (SocketException socketEx) {
            disconnect();
            var ftpSocketExceptionEx = "FTP - socket exception";
            listener.publish(ftpSocketExceptionEx);
            throw new FtpClientException(ftpSocketExceptionEx);
        } catch (IOException e) {
            log.warn("IOException: {}", e.getMessage());
            disconnect();
            var ftpConnectionProblem = "FTP connection trouble.";
            listener.publish(ftpConnectionProblem);
            throw new FtpClientException(ftpConnectionProblem);
        }
    }


    /**
     * @return temporary file containing yaml file
     * @throws FtpClientException when tmp is not possible to create
     */
    public synchronized byte[] fetchFile() {
        if (ftpApacheClient == null) {
            throw new FtpClientException("FTP conneciton is not established");
        }

        listener.publish("Downloading file ...");

        // Transfer file
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            if (!ftpApacheClient.retrieveFile(ftpConfig.file(), baos)) {
                var msg = "Cannot download ftp file %s".formatted(ftpConfig.file());
                listener.publish(msg);
                throw new FtpClientException(msg);
            }
            listener.publish("File downloaded");
            return baos.toByteArray();
        } catch (IOException e) {
            throw new FtpClientException("Cannot download ftp file %s".formatted(ftpConfig.file()), e);
        }
    }
}
