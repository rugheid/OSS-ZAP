package org.parosproxy.paros.extension.filter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.filter.classifier.ContentClassifier;
import org.parosproxy.paros.extension.filter.classifier.ContentClassifier.Classification;
import org.parosproxy.paros.extension.filter.classifier.InappropriateTermClassifier;
import org.parosproxy.paros.network.HttpMessage;

import java.io.IOException;
import java.io.InputStream;

public class FilterInappropriateContent extends FilterAdaptor {

    private ContentClassifier classifier = new InappropriateTermClassifier();

    @Override
    public int getId() {
        return 307;
    }

    @Override
    public String getName() {
        return Constant.messages.getString("filter.inappropriatecontent.name");
    }

    @Override
    public void onHttpRequestSend(HttpMessage httpMessage) {}

    @Override
    public void onHttpResponseReceive(HttpMessage httpMessage) {
        if (!classifier.canClassify(httpMessage)) return;

        Classification classification = classifier.classify(httpMessage);

        if (classification.classified) {
            String blockedPageHTML = null;
            try {
                blockedPageHTML = IOUtils.toString(getClass().getResourceAsStream("/resource/oss/blocked.html"));
            } catch (IOException e) {
                System.out.println("Loading blocked HTML page failed! Using simple string instead.");
                blockedPageHTML = "<h1>Blocked</h1>";
            }
            blockedPageHTML = blockedPageHTML.replace("<reasons>", StringUtils.join(classification.reasons, ", "));
            httpMessage.setResponseBody(blockedPageHTML);
        }
    }
}
