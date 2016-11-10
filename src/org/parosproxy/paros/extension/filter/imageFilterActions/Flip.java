package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.apache.commons.io.IOUtils;
import org.parosproxy.paros.network.HttpMessage;

import java.io.IOException;

public class Flip extends ImageFilterAction {

    @Override
    public void onHttpResponseReceive(HttpMessage msg) {
        if (msg.getResponseHeader().isEmpty() || msg.getResponseHeader().isImage() || msg.getResponseBody().length() == 0) {
            return;
        }

        try {
            String css = IOUtils.toString(getClass().getResourceAsStream("/resource/oss/flipimages.css"));
            String result = msg.getResponseBody().toString().replaceAll("<head>",
                    "<head>\n<style media=\"screen\" type=\"text/css\">\n" + css + "</style>");
            msg.getResponseBody().setBody(result);
            msg.getResponseHeader().setContentLength(msg.getResponseBody().length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
