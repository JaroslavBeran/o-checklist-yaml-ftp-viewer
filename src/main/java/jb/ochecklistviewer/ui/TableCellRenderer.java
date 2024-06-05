package jb.ochecklistviewer.ui;

import ca.odell.glazedlists.swing.AdvancedTableModel;
import jb.ochecklistviewer.data.RunnerData;

import java.io.Serial;


public class TableCellRenderer extends TableCellRendererBase {

    @Serial
    private static final long serialVersionUID = -8027195283731284199L;


    public TableCellRenderer(AdvancedTableModel<RunnerData> tableModel) {
        super(tableModel);
    }
}
