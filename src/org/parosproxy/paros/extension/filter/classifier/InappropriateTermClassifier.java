package org.parosproxy.paros.extension.filter.classifier;

import org.apache.commons.lang.StringUtils;
import org.parosproxy.paros.network.HttpMessage;

import org.parosproxy.paros.extension.filter.classifier.InappropriateTermParser.Term;
import org.parosproxy.paros.extension.filter.classifier.InappropriateTermParser.InappropriateTermFileContent;

public class InappropriateTermClassifier implements ContentClassifier {

    @Override
    public boolean canClassify(HttpMessage message) {
        return !(message.getResponseHeader().isImage() || message.getResponseHeader().isEmpty());
    }

    @Override
    public Classification classify(HttpMessage message) {
        InappropriateTermFileContent fileContent = readInappropriateTermsFromFile();
        Classification classification = new Classification();
        if (fileContent == null) return classification;
        int score = 0;
        for (Term term: fileContent.terms) {
            int occurrences = StringUtils.countMatches(message.getResponseBody().toString(), term.term);
            if (occurrences > 0) {
                classification.reasons.addAll(term.reasons);
                score += term.score;
            }
        }
        classification.classifiedInappropriate = score > fileContent.threshold;
        return classification;
    }


    // INAPPROPRIATE TERM PARSING

    private final String fileName = "inappropriate_terms", extension = "csv";

    private InappropriateTermFileContent fileContent;

    private InappropriateTermFileContent readInappropriateTermsFromFile() {
        if (fileContent == null) {
            InappropriateTermParser parser = new CSVParser();
            fileContent = parser.parseFileWithName("/resource/oss/" + fileName + "." + extension);
        }
        return fileContent;
    }
}
