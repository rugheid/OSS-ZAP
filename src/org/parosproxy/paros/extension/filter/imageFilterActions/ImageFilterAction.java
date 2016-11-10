package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.parosproxy.paros.network.HttpMessage;

public abstract class ImageFilterAction {

    public abstract void onHttpResponseReceive(HttpMessage msg);
}
