package org.parosproxy.paros.extension.filter.classifier;

import org.parosproxy.paros.network.HttpMessage;

import java.util.List;
import java.util.Set;

public interface ContentClassifier {

    Classification classify(HttpMessage message);

    class Classification {
        final int score;
        final Set<String> reasons;

        Classification(int score, Set<String> reasons) {
            this.score = score;
            this.reasons = reasons;
        }
    }
}
