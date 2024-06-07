package jb.ochecklistviewer.ui;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import ca.odell.glazedlists.impl.sort.ComparableComparator;
import jb.ochecklistviewer.data.Formatter;
import jb.ochecklistviewer.data.RunnerData;

import java.util.Comparator;


public class ChecklistEntryTableFormat implements AdvancedTableFormat<RunnerData>, WritableTableFormat<RunnerData> {

    @Override
    public int getColumnCount() {
        return 13;
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
            case 12 -> UIConstants.ColumnIdentification.SOLVED.getLabel();
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
            case 12 -> runnerData.isSolved();
            default -> throw new IllegalStateException("Unexpected column: " + column);
        };
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 -> String.class;
            case 12 -> Boolean.class;
            default -> throw new IllegalStateException("Unexpected column: " + column);
        };
    }


    @Override
    public Comparator<?> getColumnComparator(int column) {
        return new ComparableComparator();
    }

    @Override
    public boolean isEditable(RunnerData baseObject, int column) {
        return column == 12;
    }

    @Override
    public RunnerData setColumnValue(RunnerData baseObject, Object editedValue, int column) {
        baseObject.setSolved((boolean) editedValue);
        return baseObject;
    }
}
