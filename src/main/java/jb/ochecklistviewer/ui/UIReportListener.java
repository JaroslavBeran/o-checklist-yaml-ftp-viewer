package jb.ochecklistviewer.ui;

import jb.ochecklistviewer.data.RunnerData;
import jb.ochecklistviewer.data.Statistics;

import java.util.List;


public interface UIReportListener {

    void update(List<RunnerData> runnerData, Statistics statistics);
}
