package org.parosproxy.paros.extension.filter.classifier;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface InappropriateTermParser {

    InappropriateTermFileContent parseFileWithName(String fileName);

    class InappropriateTermFileContent {
        public final int threshold;
        public final List<Term> terms;

        public InappropriateTermFileContent(int threshold, List<Term> terms) {
            this.threshold = threshold;
            this.terms = terms;
        }
    }

    class Term {
        public final String term;
        public final int score;
        public final Set<String> reasons;

        public Term(String term, int score, Set<String> reasons) {
            this.term = term;
            this.score = score;
            this.reasons = reasons;
        }
    }
}
