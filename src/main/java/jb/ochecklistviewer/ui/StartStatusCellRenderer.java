package jb.ochecklistviewer.ui;

import ca.odell.glazedlists.swing.AdvancedTableModel;
import jb.ochecklistviewer.data.RunnerData;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;


public class StartStatusCellRenderer extends TableCellRendererBase {

    @Serial
    private static final long serialVersionUID = 1282163621289152313L;


    public StartStatusCellRenderer(AdvancedTableModel<RunnerData> tableModel) {
        super(tableModel);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        RunnerData runner = getTableModel().getElementAt(row);
        Color colour = switch (runner.getRunner().getStartStatus()) {
            case STARTED_OK -> Color.GREEN.darker();
            case DNS -> Color.RED;
            case LATE_START -> new Color(0xff, 0x92, 0x00);
        };
        cellComponent.setBackground(colour);
        return cellComponent;
    }
}
