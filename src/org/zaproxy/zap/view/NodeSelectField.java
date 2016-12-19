package org.zaproxy.zap.view;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.utils.ZapTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class NodeSelectField {


    // PROPERTIES

    private ZapTextField textField;
    private JButton selectButton;
    private JPanel panel;
    private Consumer<SiteNode> selectedNodeConsumer;
    private Window parent;

    public ZapTextField getTextField() {
        return this.textField;
    }

    public JPanel getPanel() {
        return this.panel;
    }

    public void setSelectedNodeConsumer(Consumer<SiteNode> selectedNodeConsumer) {
        this.selectedNodeConsumer = selectedNodeConsumer;
    }

    public void setParent(Window parent) {
        this.parent = parent;
    }

    public void setSelectedNode(SiteNode node) {
        this.textField.setText(StandardFieldsUtils.getNodeText(node));
    }

    public void addActionListener(ActionListener actionListener) {
        this.selectButton.addActionListener(actionListener);
    }


    // CONSTRUCTOR

    public NodeSelectField(SiteNode selectedNode, final boolean allowRoot) {
        this.textField = new ZapTextField();
        this.selectButton = new JButton(Constant.messages.getString("all.button.select"));
        this.selectButton.setIcon(new ImageIcon(View.class.getResource("/resource/icon/16/094.png"))); // Globe icon
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            // Keep a local copy so that we can always select the last node chosen
            SiteNode node = selectedNode;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                NodeSelectDialog nsd = new NodeSelectDialog(NodeSelectField.this.parent);
                nsd.setAllowRoot(allowRoot);
                SiteNode node = nsd.showDialog(this.node);
                if (node != null) {
                    NodeSelectField.this.setSelectedNode(node);
                    this.node = node;
                    NodeSelectField.this.selectedNodeConsumer.accept(node);
                }
            }
        });
        this.panel = new JPanel();
        this.panel.setLayout(new GridBagLayout());
        this.panel.add(this.textField, LayoutHelper.getGBC(0, 0, 1, 1.0D, 0.0D, GridBagConstraints.BOTH, new Insets(4,4,4,4)));
        this.panel.add(selectButton, LayoutHelper.getGBC(1, 0, 1, 0.0D, 0.0D, GridBagConstraints.BOTH, new Insets(4,4,4,4)));
    }

}
