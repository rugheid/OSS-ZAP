package org.parosproxy.paros.extension.filter.classifier;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

            }
        } catch (IOException e) {
            // TODO: Handle this error
        }

        return null;
    }

    private void parseLine(String line) {
        if (line.startsWith("#")) return;

        String[] parts = line.split(";");
        if (parts.length != 3) return;

        String term = parts[0];
        int score = Integer.parseInt(parts[1]);
        Set<String> reasons = new HashSet<>(Arrays.asList(parts[2].split(",")));
        fileContent.terms.add(new Term(term, score, reasons));
    }
}
