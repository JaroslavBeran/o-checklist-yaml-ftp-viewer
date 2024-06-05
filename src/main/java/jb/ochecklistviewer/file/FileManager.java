package jb.ochecklistviewer.file;

import jb.ochecklistviewer.ui.UIReportListener;
import jb.ochecklistviewer.ui.UIStatusListener;

import java.io.File;


public interface FileManager {

    void readFile(File file, UIReportListener UIReportListener, UIStatusListener UIStatusListener);

    File getLastFile();
}
