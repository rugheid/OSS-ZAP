package org.parosproxy.paros.extension.filter.classifier;

import org.parosproxy.paros.network.HttpMessage;

import java.util.Set;

public interface ContentClassifier {

    boolean canClassify(HttpMessage message);

    Classification classify(HttpMessage message);

    class Classification {
        public final boolean classifiedInappropriate;
        public final Set<String> reasons;

        Classification(boolean classifiedInappropriate, Set<String> reasons) {
            this.classifiedInappropriate = classifiedInappropriate;
            this.reasons = reasons;
        }
    }
}
