package org.parosproxy.paros.extension.filter.classifier;

import org.parosproxy.paros.network.HttpMessage;

import java.util.HashSet;
import java.util.Set;

public class InappropriateTermClassifier implements ContentClassifier {

    @Override
    public boolean canClassify(HttpMessage message) {
        return !(message.getResponseHeader().isImage() || message.getResponseHeader().isEmpty());
    }

    @Override
    public Classification classify(HttpMessage message) {
        // TODO: Implement this
        Set<String> reasons = new HashSet<>();
        reasons.add("You shall not pass! ~Gandalf");
        return new Classification(true, reasons);
    }
}
