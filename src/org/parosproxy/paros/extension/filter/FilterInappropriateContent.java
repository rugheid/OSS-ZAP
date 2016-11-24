package org.parosproxy.paros.extension.filter;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.filter.classifier.ContentClassifier;
import org.parosproxy.paros.extension.filter.classifier.ContentClassifier.Classification;
import org.parosproxy.paros.extension.filter.classifier.InappropriateTermClassifier;
import org.parosproxy.paros.network.HttpMessage;

public class FilterInappropriateContent extends FilterAdaptor {

    ContentClassifier classifier = new InappropriateTermClassifier();

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
            // TODO: Return automatic page here with reasons
            httpMessage.getResponseBody().setBody("<h1>Blocked!</h1>");
        }
    }
}
