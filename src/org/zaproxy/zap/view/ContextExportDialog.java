/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2014 The ZAP Development Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.*;

import org.apache.commons.configuration.ConfigurationException;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.model.Context;
import org.zaproxy.zap.utils.ZapTextField;
import org.zaproxy.zap.view.widgets.ContextSelectComboBox;

public class ContextExportDialog extends StandardFieldsDialog {

	private static final long serialVersionUID = 1L;
	
	private static final String CONTEXT_FIELD = "context.import.label.context"; 
	private static final String DIR_FIELD = "context.import.label.dir"; 
	private static final String FILE_FIELD = "context.import.label.file"; 
	private static final String OVERWRITE_FIELD = "context.import.label.overwrite"; 

	private static final String CONTEXT_EXT = ".context";

	public ContextExportDialog(Frame owner) {
		super(owner, "context.import.title", new Dimension(400,250));
		this.addField(CONTEXT_FIELD, StandardFieldsFactory.get().createContextSelectField(null));
		this.addFileSelectField(DIR_FIELD, Constant.getContextsDir(), JFileChooser.DIRECTORIES_ONLY, null);
		this.addField(FILE_FIELD, StandardFieldsFactory.get().createTextField(null));
		this.addField(OVERWRITE_FIELD, StandardFieldsFactory.get().createCheckBoxField(false));
		
		StandardFieldsUtils.addFieldListener(this.getField(CONTEXT_FIELD), e -> {
            Context ctx = ((ContextSelectComboBox)getField(CONTEXT_FIELD)).getSelectedContext();
            if (ctx != null) {
                String fileName = ctx.getName() + CONTEXT_EXT;
                StandardFieldsUtils.setFieldValue(getField(FILE_FIELD), fileName);
            }
        });
	}
	
	private File getSelectedFile() {
		if (StandardFieldsUtils.isEmptyField(getField(DIR_FIELD)) || StandardFieldsUtils.isEmptyField(getField(FILE_FIELD))) {
			return null;
		}
		String dirValue = ((ZapTextField)this.getField(DIR_FIELD)).getText();
		String fileValue = ((ZapTextField)this.getField(FILE_FIELD)).getText();
		return new File (dirValue, fileValue);
	}

	@Override
	public void save() {
		try {
			Model.getSingleton().getSession().exportContext(((ContextSelectComboBox)getField(CONTEXT_FIELD)).getSelectedContext(), getSelectedFile());
		} catch (ConfigurationException e) {
			View.getSingleton().showWarningDialog(this, 
					MessageFormat.format(Constant.messages.getString("context.import.error"), e.getMessage()));
		}
	}

	@Override
	public String validateFields() {
		File f = this.getSelectedFile();
		if (((ContextSelectComboBox)getField(CONTEXT_FIELD)).getSelectedContext() == null) {
			return Constant.messages.getString("context.import.error.nocontext");
		}
		if (f == null) {
			return Constant.messages.getString("context.import.error.nofile");
		} else if (f.exists() & ! ((JCheckBox)getField(OVERWRITE_FIELD)).isSelected()) {
			return Constant.messages.getString("context.import.error.exists");
		} else if (! f.getParentFile().canWrite()) {
			return Constant.messages.getString("context.import.error.noaccess");
		}
		return null;
	}

}
