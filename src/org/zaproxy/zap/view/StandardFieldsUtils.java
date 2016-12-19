package org.zaproxy.zap.view;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.SiteNode;
import org.zaproxy.zap.model.Target;
import org.zaproxy.zap.utils.ZapTextArea;
import org.zaproxy.zap.utils.ZapTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
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

    /**
     * Returns the text representation of the given {@code target}.
     * <p>
     * If the {@code target} is not {@code null} it returns:
     * <ol>
     * <li>the URI, if it has a start node with an history reference;</li>
     * <li>"Context: " followed by context's name, if it has a context;</li>
     * <li>"Everything in scope", if it's only in scope.</li>
     * </ol>
     * For remaining cases it returns {@code null}.
     *
     * @param target the target whose text representation will be returned
     * @return the text representation of the given {@code target}, might be {@code null}
     * @since 2.4.2
     * @see Target#getStartNode()
     * @see Target#getContext()
     * @see Target#isInScopeOnly()
     */
    public static String getTargetText(Target target) {
        if (target != null) {
            if (target.getStartNode() != null) {
                return getNodeText(target.getStartNode());
            } else if (target.getContext() != null) {
                return Constant.messages.getString("context.prefixName", target.getContext().getName());
            } else if (target.isInScopeOnly()) {
                return Constant.messages.getString("context.allInScope");
            }
        }
        return null;
    }

    public static String getNodeText(SiteNode node) {
        if (node != null && node.getHistoryReference() != null) {
            String url = node.getHistoryReference().getURI().toString();
            if (node.isLeaf() && url.endsWith("/")) {
                // String off the slash so we dont match a non leaf
                // node with the same name
                url = url.substring(0, url.length()-1);
            } else if (! node.isLeaf() && ! url.endsWith("/")) {
                // Add the slash to show its a non leaf node
                url = url + "/";
            }
            return url;
        }
        return "";
    }

    public static void addFieldListener(Component c, ActionListener listener) {
        if (c != null) {
            if (c instanceof ZapTextField) {
                ((ZapTextField)c).addActionListener(listener);
            } else if (c instanceof JPasswordField) {
                ((JPasswordField)c).addActionListener(listener);
            } else if (c instanceof JComboBox) {
                ((JComboBox<?>)c).addActionListener(listener);
            } else if (c instanceof JCheckBox) {
                ((JCheckBox)c).addActionListener(listener);
            }
        }
    }

    public static void addFieldListener(Component c, MouseAdapter listener) {
        if (c != null) {
            if (c instanceof ZapTextField) {
                ((ZapTextField)c).addMouseListener(listener);
            } else if (c instanceof ZapTextArea) {
                ((ZapTextArea)c).addMouseListener(listener);
            } else if (c instanceof JPasswordField) {
                ((JPasswordField)c).addMouseListener(listener);
            } else if (c instanceof JComboBox) {
                ((JComboBox<?>)c).addMouseListener(listener);
            }
        }
    }
}
