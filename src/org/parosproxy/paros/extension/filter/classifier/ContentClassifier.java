package org.parosproxy.paros.extension.filter.classifier;

import org.parosproxy.paros.network.HttpMessage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface ContentClassifier {

    boolean canClassify(HttpMessage message);

    Classification classify(HttpMessage message);

    class Classification {
        private boolean classifiedInappropriate;
        private Set<String> reasons;

        public boolean isClassifiedInappropriate() {
            return classifiedInappropriate;
        }

        public void setClassifiedInappropriate(boolean classifiedInappropriate) {
            this.classifiedInappropriate = classifiedInappropriate;
        }

        public Set<String> getReasons() {
            return reasons;
        }

        public void addReasons(Collection<? extends String> collection) {
            reasons.addAll(collection);
        }

        Classification() {
            this(false, new HashSet<>());
        }

        Classification(boolean classifiedInappropriate, Set<String> reasons) {
            this.classifiedInappropriate = classifiedInappropriate;
            this.reasons = reasons;
        }
    }
}
