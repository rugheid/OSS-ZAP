package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.parosproxy.paros.network.HttpMessage;

public abstract class ImageFilterAction {

    private Boolean enabled;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    public abstract void onHttpResponseReceive(HttpMessage msg);
}
