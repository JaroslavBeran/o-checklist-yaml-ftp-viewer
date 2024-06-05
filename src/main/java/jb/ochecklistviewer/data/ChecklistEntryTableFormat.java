package jb.ochecklistviewer.data;

import ca.odell.glazedlists.gui.TableFormat;
import jb.ochecklistviewer.ui.UIConstants;


public class ChecklistEntryTableFormat implements TableFormat<RunnerData> {

    @Override
    public int getColumnCount() {
        return 12;
    }


    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> UIConstants.ColumnIdentification.ID.getLabel();
            case 1 -> UIConstants.ColumnIdentification.NAME.getLabel();
            case 2 -> UIConstants.ColumnIdentification.ORG.getLabel();
            case 3 -> UIConstants.ColumnIdentification.CARD.getLabel();
            case 4 -> UIConstants.ColumnIdentification.START.getLabel();
            case 5 -> UIConstants.ColumnIdentification.CLASS.getLabel();
            case 6 -> UIConstants.ColumnIdentification.START_STATUS.getLabel();
            case 7 -> UIConstants.ColumnIdentification.NEW_CARD.getLabel();
            case 8 -> UIConstants.ColumnIdentification.NEW_CARD_CHANGED.getLabel();
            case 9 -> UIConstants.ColumnIdentification.DNS.getLabel();
            case 10 -> UIConstants.ColumnIdentification.LATE_START.getLabel();
            case 11 -> UIConstants.ColumnIdentification.COMMENT.getLabel();
            default -> throw new IllegalStateException("Unexpected column: " + column);
        };
    }


    @Override
    public Object getColumnValue(RunnerData runnerData, int column) {
        return switch (column) {
            case 0 -> Formatter.formatId(runnerData);
            case 1 -> Formatter.formatName(runnerData);
            case 2 -> Formatter.formatOrg(runnerData);
            case 3 -> Formatter.formatCard(runnerData);
            case 4 -> Formatter.formatStartTime(runnerData);
            case 5 -> Formatter.formatClassName(runnerData);
            case 6 -> Formatter.formatStartStatus(runnerData);
            case 7 -> Formatter.formatNewCard(runnerData);
            case 8 -> Formatter.formatNewCardChanged(runnerData);
            case 9 -> Formatter.formatDnsChanged(runnerData);
            case 10 -> Formatter.formatLateStart(runnerData);
            case 11 -> Formatter.formatComment(runnerData);
            default -> throw new IllegalStateException("Unexpected column: " + column);
        };
    }
}
