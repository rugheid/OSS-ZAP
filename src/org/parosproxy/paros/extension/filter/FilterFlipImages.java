package org.parosproxy.paros.extension.filter;

import org.apache.commons.io.IOUtils;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.network.HttpMessage;

import java.io.IOException;

/**
 * A filter that flips all images on the web page vertically.
 */
public class FilterFlipImages extends FilterAdaptor {

    @Override
    public int getId() {
        return 3713;
    }

    @Override
    public String getName() {
        return Constant.messages.getString("filter.flipimages.name");
    }

    @Override
    public void onHttpRequestSend(HttpMessage httpMessage) {}

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
