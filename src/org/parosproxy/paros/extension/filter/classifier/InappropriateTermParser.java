package org.parosproxy.paros.extension.filter.classifier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface InappropriateTermParser {

    InappropriateTermFileContent parseFileWithName(String fileName);

    class InappropriateTermFileContent {
        public final int threshold;
        public final List<Term> terms;

        InappropriateTermFileContent() {
            this(0, new ArrayList<>());
        }

        InappropriateTermFileContent(int threshold, List<Term> terms) {
            this.threshold = threshold;
            this.terms = terms;
        }
    }

    class Term {
        public final String term;
        public final int score;
        public final Set<String> reasons;

        Term() {
            this("", 0, new HashSet<>());
        }

        Term(String term, int score, Set<String> reasons) {
            this.term = term;
            this.score = score;
            this.reasons = reasons;
        }
    }
}
