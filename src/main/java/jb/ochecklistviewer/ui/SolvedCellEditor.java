package jb.ochecklistviewer.ui;

import javax.swing.*;
import java.awt.*;

import static jb.ochecklistviewer.ui.UIConstants.TABLE_ROW_SELECTION_BACKGROUND_COLOR;


public class SolvedCellEditor extends DefaultCellEditor {

    public SolvedCellEditor() {
        super(new JCheckBox());
        JCheckBox checkBox = (JCheckBox) getComponent();
        checkBox.setHorizontalAlignment(JLabel.CENTER);
        checkBox.setBorderPainted(false);
        checkBox.setOpaque(true);
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        JCheckBox checkBox = (JCheckBox) getComponent();
        checkBox.setBackground(TABLE_ROW_SELECTION_BACKGROUND_COLOR); // It is expected that each click causes selection of the row
        checkBox.setSelected(value != null && (Boolean) value);
        return checkBox;
    }
}
