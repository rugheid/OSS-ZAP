package org.parosproxy.paros.extension.filter.classifier;

import org.parosproxy.paros.network.HttpMessage;

import java.util.HashSet;

public class InappropriateTermClassifier implements ContentClassifier {

    @Override
    public boolean canClassify(HttpMessage message) {
        return !(message.getResponseHeader().isImage() || message.getResponseHeader().isEmpty());
    }

    @Override
    public Classification classify(HttpMessage message) {
        // TODO: Implement this
        return new Classification(true, new HashSet<>());
    }
}
