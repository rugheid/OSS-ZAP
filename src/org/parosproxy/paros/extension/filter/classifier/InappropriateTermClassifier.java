package org.parosproxy.paros.extension.filter.classifier;

import org.parosproxy.paros.network.HttpMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.parosproxy.paros.extension.filter.classifier.InapproprateTermParser.Term;

public class InappropriateTermClassifier implements ContentClassifier {

    @Override
    public boolean canClassify(HttpMessage message) {
        return !(message.getResponseHeader().isImage() || message.getResponseHeader().isEmpty());
    }

    @Override
    public Classification classify(HttpMessage message) {

        // TODO: Parse here correctly
        List<Term> terms = new CSVParser().parseFile(null);

        for (Term term: terms) {
            // TODO: Count occurrences here
        }

        return new Classification(true, new HashSet<>());
    }
}
