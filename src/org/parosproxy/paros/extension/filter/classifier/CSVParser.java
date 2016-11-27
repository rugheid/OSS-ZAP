package org.parosproxy.paros.extension.filter.classifier;

import java.io.InputStream;
import java.util.List;

public class CSVParser implements InappropriateTermParser {

    static {
        CSVParser parser = new CSVParser();
        InappropriateTermParserFactory.getSharedInstance().registerParserForExtension(parser, "csv");
    }

    @Override
    public List<Term> parseFile(InputStream input) {
        // TODO: Implement this
        return null;
    }
}
