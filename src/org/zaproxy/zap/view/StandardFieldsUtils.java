package org.zaproxy.zap.view;

import javax.swing.*;
import java.util.List;

public class StandardFieldsUtils {

    public static void setComboFields(JComboBox<String> comboBox, List<String> choices, String value) {
        if (comboBox == null) return;
        comboBox.removeAllItems();
        for (String str : choices) {
            comboBox.addItem(str);
        }
        if (value != null) {
            comboBox.setSelectedItem(value);
        }
    }
}
