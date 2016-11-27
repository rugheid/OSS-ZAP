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

        String fileName = "file_name_here", extension = "csv";

        InappropriateTermParser parser = InappropriateTermParserFactory.getSharedInstance().getParserForExtension(extension);
        InputStream file = getClass().getResourceAsStream(fileName + "." + extension);
        List<Term> terms = parser.parseFile(file);

        for (Term term: terms) {
            // TODO: Count occurrences here
        }

        return new Classification(true, new HashSet<>());
    }
}
