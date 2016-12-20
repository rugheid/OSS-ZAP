package org.zaproxy.zap.view;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.model.Context;
import org.zaproxy.zap.utils.ZapNumberSpinner;
import org.zaproxy.zap.utils.ZapTextArea;
import org.zaproxy.zap.utils.ZapTextField;
import org.zaproxy.zap.view.widgets.ContextSelectComboBox;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StandardFieldsFactory {

    // SINGLETON

    private static StandardFieldsFactory singleton;

    public static StandardFieldsFactory get() {
        if (singleton == null) {
            singleton = new StandardFieldsFactory();
        }
        return singleton;
    }


    // FACTORY METHODS

    public ZapTextField createTextField(String value) {
        ZapTextField field = new ZapTextField();
        if (value != null) {
            field.setText(value);
        }
        return field;
    }

    public JPasswordField createPasswordField(String value) {
        JPasswordField field = new JPasswordField();
        if (value != null) {
            field.setText(value);
        }
        return field;
    }

    public ZapTextArea createMultilineField(String value) {
        ZapTextArea field = new ZapTextArea();
        field.setLineWrap(true);
        if (value != null) {
            field.setText(value);
        }
        return field;
    }

    public JComboBox<String> createComboField(List<String> choices, String value) {
        return this.createComboField(choices, value, false);
    }

    public JComboBox<String> createComboField(List<String> choices, String value, boolean editable) {
        JComboBox<String> field = new JComboBox<>();
        field.setEditable(editable);
        for (String label : choices) {
            field.addItem(label);
        }
        if (value != null) {
            field.setSelectedItem(value);
        }
        return field;
    }

    public JComboBox<Integer> createComboField(int[] choices, int value) {
        JComboBox<Integer> field = new JComboBox<>();
        for (int label : choices) {
            field.addItem(label);
        }
        if (value >= 0) {
            field.setSelectedItem(value);
        }
        return field;
    }

    public ZapNumberSpinner createNumberField(Integer min, Integer max, int value) {
        ZapNumberSpinner field = new ZapNumberSpinner(min, value, max);
        return field;
    }

    public JCheckBox createCheckBoxField(boolean value) {
        JCheckBox field = new JCheckBox();
        field.setSelected(value);
        return field;
    }

    public ContextSelectComboBox createContextSelectField(Context selectedContext){
        ContextSelectComboBox field = new ContextSelectComboBox();
        if (selectedContext != null) {
            field.setSelectedItem(selectedContext);
        }
        return field;
    }

    public NodeSelectField createNodeSelectField(final SiteNode value, final boolean allowRoot) {
        NodeSelectField nodeSelectField = new NodeSelectField(value, allowRoot);
        return nodeSelectField;
    }
}
