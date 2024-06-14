package jb.ochecklistviewer.data;

import ca.odell.glazedlists.TextFilterator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class RunnerFilterator implements TextFilterator<RunnerData> {

    @Override
    public void getFilterStrings(List<String> baseList, RunnerData runnerData) {
        baseList.add(Formatter.formatId(runnerData));
        baseList.add(StringUtils.stripAccents(Formatter.formatName(runnerData)));
        baseList.add(Formatter.formatName(runnerData));
        baseList.add(Formatter.formatOrg(runnerData));
        baseList.add(Formatter.formatCard(runnerData));
        baseList.add(Formatter.formatStartTime(runnerData));
        baseList.add(Formatter.formatClassName(runnerData));
        baseList.add(Formatter.formatStartStatus(runnerData));
        baseList.add(Formatter.formatNewCard(runnerData));
        baseList.add(Formatter.formatNewCardChanged(runnerData));
        baseList.add(Formatter.formatDnsChanged(runnerData));
        baseList.add(Formatter.formatLateStart(runnerData));
        baseList.add(StringUtils.stripAccents(Formatter.formatComment(runnerData)));
        baseList.add(Formatter.formatComment(runnerData));
    }
}
