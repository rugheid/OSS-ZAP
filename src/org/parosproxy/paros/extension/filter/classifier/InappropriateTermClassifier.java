package org.parosproxy.paros.extension.filter.classifier;

import org.parosproxy.paros.network.HttpMessage;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import org.parosproxy.paros.extension.filter.classifier.InappropriateTermParser.Term;

public class InappropriateTermClassifier implements ContentClassifier {

    @Override
    public boolean canClassify(HttpMessage message) {
        return !(message.getResponseHeader().isImage() || message.getResponseHeader().isEmpty());
    }

    @Override
    public Classification classify(HttpMessage message) {

        List<Term> terms = readInappropriateTermsFromFile();
        for (Term term: terms) {
            // TODO: Count occurrences here
        }

        return new Classification(true, new HashSet<>());
    }


    // INAPPROPRIATE TERM PARSING

    // TODO: Move this to a configuration file?
    private final String fileName = "inappropriate_terms", extension = "csv";

    private List<Term> inappropriateTerms;

    private List<Term> readInappropriateTermsFromFile() {
        if (inappropriateTerms == null) {
            InappropriateTermParser parser = new CSVParser();
            inappropriateTerms = parser.parseFileWithName(fileName + "." + extension);
        }
        return inappropriateTerms;
    }
}
