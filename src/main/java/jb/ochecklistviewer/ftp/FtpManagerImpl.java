package jb.ochecklistviewer.ftp;

import jb.ochecklistviewer.config.FtpConfig;
import jb.ochecklistviewer.data.ChecklistReport;
import jb.ochecklistviewer.data.StatisticUtils;
import jb.ochecklistviewer.ui.UIReportListener;
import jb.ochecklistviewer.ui.UIStatusListener;
import jb.ochecklistviewer.yaml.YamlReaderWriter;
import jb.ochecklistviewer.yaml.YamlReaderWritterException;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class FtpManagerImpl implements FtpManager {

    private static final UIReportListener DUMMY_UI_REPORT_LISTENER = (runnerData, statistics) -> {
    };
    private static final UIStatusListener DUMMY_UI_STATUS_LISTENER = str -> {
    };


    private final FtpHandler ftpClient;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> scheduledFuture;
    private UIReportListener uiReportListener = DUMMY_UI_REPORT_LISTENER;
    private UIStatusListener uiStatusListener = DUMMY_UI_STATUS_LISTENER;


    public FtpManagerImpl() {
        ftpClient = new FtpHandler(System.out::println);
    }

    @Override
    public synchronized void startPolling(FtpConfig ftpConfig, UIReportListener uiReportListener, UIStatusListener uiStatusListener) {
        if (scheduledFuture != null) {
            return; // Cannot run another "future"
        }
        this.uiReportListener = uiReportListener;
        this.uiStatusListener = uiStatusListener;

        var ftpTask = new FtpTask(ftpConfig);
        scheduledFuture = executor.scheduleAtFixedRate(ftpTask, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void stopPolling() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        uiReportListener = DUMMY_UI_REPORT_LISTENER;
        scheduledFuture = null;
        ftpClient.disconnect();
        uiStatusListener.update("FTP disconnected");
        uiStatusListener = DUMMY_UI_STATUS_LISTENER;
    }


    enum FtpStep {
        WAIT_FOR_RECONNECT, CONNECTION_REQUIRED, COUNTDOWN, FILE_FETCHING
    }

    @RequiredArgsConstructor
    class FtpTask implements Runnable {

        private final static int COUNTDOWN_NUMBER = 10;
        private final static int CONNECTION_ERROR_CNT_MAX = 3;
        private final static int RECONNECT_CNT = 10;
        private final FtpConfig ftpConfig;
        FtpStep ftpStep = FtpStep.CONNECTION_REQUIRED;
        int countDown = COUNTDOWN_NUMBER;
        int connectionErrCnt = 0;
        int reconnectCnt = RECONNECT_CNT;


        @Override
        public void run() {
            switch (ftpStep) {
                case WAIT_FOR_RECONNECT:
                    uiStatusListener.update("FTP next reconnect trial after %d secs".formatted(reconnectCnt));
                    reconnectCnt--;
                    if (reconnectCnt <= 0) {
                        ftpStep = FtpStep.CONNECTION_REQUIRED;
                        reconnectCnt = RECONNECT_CNT;
                    }
                    break;

                case CONNECTION_REQUIRED:
                    // Connect
                    try {
                        uiStatusListener.update("FTP is connecting ...");
                        ftpClient.connect(ftpConfig);
                        ftpStep = FtpStep.FILE_FETCHING;
                        uiStatusListener.update("FTP is connected.");
                    } catch (FtpClientException e) {
                        uiStatusListener.update("FTP was not connected: " + e.getMessage());
                        connectionErrCnt++;
                        if (connectionErrCnt >= CONNECTION_ERROR_CNT_MAX) {
                            ftpStep = FtpStep.WAIT_FOR_RECONNECT;
                            reconnectCnt = RECONNECT_CNT;
                            connectionErrCnt = 0;
                        } else {
                            ftpStep = FtpStep.CONNECTION_REQUIRED;
                        }
                    }
                    break;

                case COUNTDOWN:
                    uiStatusListener.update("FTP next file load after %d secs".formatted(countDown));
                    countDown--;
                    // System.out.println(countDown);
                    if (countDown <= 0) {
                        ftpStep = FtpStep.FILE_FETCHING;
                        countDown = ftpConfig.refresh();
                    }
                    break;

                case FILE_FETCHING:
                    // Fetch file
                    try {
                        uiStatusListener.update("FTP is loading file ...");
                        handleFtpFetch();
                        ftpStep = FtpStep.COUNTDOWN;
                    } catch (FtpClientException e) {
                        ftpClient.disconnect();
                        ftpStep = FtpStep.CONNECTION_REQUIRED;
                        uiStatusListener.update("FTP connection is broken: " + e.getMessage());
                        connectionErrCnt++;
                        if (connectionErrCnt >= CONNECTION_ERROR_CNT_MAX) {
                            ftpStep = FtpStep.WAIT_FOR_RECONNECT;
                            connectionErrCnt = 0;
                            reconnectCnt = RECONNECT_CNT;
                        } else {
                            ftpStep = FtpStep.CONNECTION_REQUIRED;
                        }
                    }
                    break;
            }
        }


        public void handleFtpFetch() {
            byte[] bytes = ftpClient.fetchFile();

            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                final ChecklistReport checklistReport = YamlReaderWriter.readReport(bais);
                var stat = StatisticUtils.calculate(checklistReport);
                uiReportListener.update(checklistReport.getRunners(), stat);
            } catch (YamlReaderWritterException | IOException e) {
                uiStatusListener.update("FTP cannot parse YAML file: " + e.getMessage());
            }
        }

    }
}
