package org.parosproxy.paros.extension.filter.classifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface InappropriateTermParser {

    InappropriateTermFileContent parseFileWithName(String fileName);

    class InappropriateTermFileContent {
        private int threshold;
        private List<Term> terms;

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public List<Term> getTerms() {
            return terms;
        }

        public void addTerm(Term term) {
            terms.add(term);
        }

        InappropriateTermFileContent() {
            this(0, new ArrayList<>());
        }

        InappropriateTermFileContent(int threshold, List<Term> terms) {
            this.threshold = threshold;
            this.terms = terms;
        }
    }

    class Term {
        private final String term;
        private final int score;
        private final Set<String> reasons;

        public String getTerm() {
            return term;
        }

        public int getScore() {
            return score;
        }

        public Set<String> getReasons() {
            return reasons;
        }

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
