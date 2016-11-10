/*
 *
 * Paros and its related class files.
 *
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 Chinotec Technologies Company
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Clarified Artistic License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Clarified Artistic License for more details.
 *
 * You should have received a copy of the Clarified Artistic License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
// ZAP: 2011/04/16 i18n
// ZAP: 2012/03/15 Changed to use StringBuilder and replaced some string concatenations
// with calls to the method append of the class StringBuilder. Reworked some code.
// ZAP: 2012/04/25 Added @Override annotation to all appropriate methods.
// ZAP: 2012/07/29 Removed incorrect (and unused) init method
// ZAP: 2013/01/25 Removed the "(non-Javadoc)" comments.
// ZAP: 2013/03/03 Issue 546: Remove all template Javadoc comments

package org.parosproxy.paros.extension.filter;

import java.util.List;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.filter.imageFilterActions.ImageFilterAction;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.network.HttpMessage;


public class FilterImages extends FilterAdaptor {


    private List<ImageFilterAction> imageFilterActions;


    @Override
    public int getId() {
        return 306;
    }

    @Override
    public String getName() {
        // TODO: Change this name
        return Constant.messages.getString("filter.setcookie.name");
    }

    @Override
    public void init(Model model) {
        super.init(model);


    }

    @Override
    public void onHttpRequestSend(HttpMessage msg) {}

    @Override
    public void onHttpResponseReceive(HttpMessage msg) {
    }
}


