package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.parosproxy.paros.network.HttpMessage;

public class Watermark extends ImageFilterAction {

    @Override
    public void onHttpResponseReceive(HttpMessage msg) {
        if (!msg.getResponseHeader().isImage()) return;

        byte[] image = msg.getResponseBody().getBytes();
        // TODO: Work magic on the image :)
        msg.setResponseBody(image);
    }
}
