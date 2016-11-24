package org.parosproxy.paros.extension.filter.classifier;

import org.parosproxy.paros.network.HttpMessage;

import java.util.Set;

public interface ContentClassifier {

    boolean canClassify(HttpMessage message);

    Classification classify(HttpMessage message);

    class Classification {
        public final boolean classified;
        public final Set<String> reasons;

        Classification(boolean classified, Set<String> reasons) {
            this.classified = classified;
            this.reasons = reasons;
        }
    }
}
