package org.parosproxy.paros.extension.filter.classifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CSVParser implements InappropriateTermParser {

    private InappropriateTermFileContent fileContent;

    @Override
    public InappropriateTermFileContent parseFileWithName(String fileName) {

        fileContent = new InappropriateTermFileContent();

        try {
            InputStream stream = getClass().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            fileContent = null;
        }

        return fileContent;
    }

    private void parseLine(String line) {
        if (line.startsWith("#")) return;

        String[] parts = line.split(";");

        if (parts.length == 3) {

            parseTermLineFromParts(parts);

        } else if (parts.length == 1) {

            String[] thresholdParts = parts[0].split(" ");
            parseThresholdLineFromParts(thresholdParts);
        }
    }

    private void parseThresholdLineFromParts(String[] parts) {
        if (parts.length != 2) return;
        if (!parts[0].equals("threshold")) return;

        fileContent.setThreshold(Integer.parseInt(parts[1]));
    }

    private void parseTermLineFromParts(String[] parts) {
        String term = parts[0];
        int score = Integer.parseInt(parts[1]);
        Set<String> reasons = new HashSet<>(Arrays.asList(parts[2].split(",")));
        fileContent.addTerm(new Term(term, score, reasons));
    }
}
