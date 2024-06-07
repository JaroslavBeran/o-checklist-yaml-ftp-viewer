package jb.ochecklistviewer.ui;

import ca.odell.glazedlists.swing.AdvancedTableModel;
import jb.ochecklistviewer.data.RunnerData;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

import static jb.ochecklistviewer.ui.UIConstants.*;


@Getter
public class TableCellRendererBase extends DefaultTableCellRenderer {

    @Serial
    private static final long serialVersionUID = -4057717710142974061L;
    private final AdvancedTableModel<RunnerData> tableModel;


    public TableCellRendererBase(AdvancedTableModel<RunnerData> tableModel) {
        this.tableModel = tableModel;
    }


    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (tableModel.getElementAt(row).isDisappearedFromYaml()) {
            Font font = cellComponent.getFont().deriveFont(Font.PLAIN);

            Map<TextAttribute, Object>  attributes = new HashMap<>(font.getAttributes());
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);

            cellComponent.setFont(cellComponent.getFont().deriveFont(attributes));
        } else {
            cellComponent.setFont(cellComponent.getFont().deriveFont(Font.PLAIN));
        }

        if (isSelected) {
            cellComponent.setBackground(TABLE_ROW_SELECTION_BACKGROUND_COLOR);
        } else {
            if (row % 2 == 0) {
                cellComponent.setBackground(TABLE_ROW_ZEBRA_DARK);
            } else {
                cellComponent.setBackground(TABLE_ROW_ZEBRA_LIGHT);
            }
        }

        return cellComponent;
    }
}
