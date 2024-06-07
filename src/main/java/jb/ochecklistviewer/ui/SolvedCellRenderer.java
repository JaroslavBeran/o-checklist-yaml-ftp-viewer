package jb.ochecklistviewer.ui;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import static jb.ochecklistviewer.ui.UIConstants.*;

public class SolvedCellRenderer extends JCheckBox implements TableCellRenderer, UIResource {

    public SolvedCellRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setBorderPainted(false);
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(TABLE_ROW_SELECTION_BACKGROUND_COLOR);
        } else {
            if (row % 2 == 0) {
                super.setBackground(TABLE_ROW_ZEBRA_DARK);
            } else {
                super.setBackground(TABLE_ROW_ZEBRA_LIGHT);
            }
        }

        setSelected(value != null && (Boolean) value);

        return this;
    }
}