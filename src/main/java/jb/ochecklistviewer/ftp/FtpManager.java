package jb.ochecklistviewer.ftp;

import jb.ochecklistviewer.config.FtpConfig;
import jb.ochecklistviewer.ui.UIReportListener;
import jb.ochecklistviewer.ui.UIStatusListener;


public interface FtpManager {

    void startPolling(FtpConfig ftpConfig, UIReportListener UIReportListener, UIStatusListener uiStatusListener);

    void stopPolling();
}
