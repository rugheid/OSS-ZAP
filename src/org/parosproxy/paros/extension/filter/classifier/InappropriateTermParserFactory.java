package org.parosproxy.paros.extension.filter.classifier;

import java.util.HashMap;
import java.util.Map;

public class InappropriateTermParserFactory {

    // SINGLETON

    private static InappropriateTermParserFactory sharedInstance = null;

    public static InappropriateTermParserFactory getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new InappropriateTermParserFactory();
        }
        return sharedInstance;
    }


    // FACTORY

    private Map<String, InappropriateTermParser> parserMap = new HashMap<>();

    public void registerParserForExtension(InappropriateTermParser parser, String extension) {
        parserMap.put(extension, parser);
    }

    public InappropriateTermParser getParserForExtension(String extension) {
        return parserMap.get(extension);
    }
}
