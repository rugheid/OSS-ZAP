package org.parosproxy.paros.extension.filter.classification;

import org.junit.Before;
import org.junit.Test;
import org.parosproxy.paros.extension.filter.classifier.CSVParser;
import org.parosproxy.paros.extension.filter.classifier.InappropriateTermParser.Term;
import org.parosproxy.paros.extension.filter.classifier.InappropriateTermParser.InappropriateTermFileContent;

import static org.junit.Assert.*;

public class CSVParserTest {

    private CSVParser csvParser;

    @Before
    public void setup() {
        csvParser = new CSVParser();
    }

    @Test
    public void testParseFile() {
        InappropriateTermFileContent fileContent = csvParser.parseFileWithName("resources/oss/terms_test.csv");
        assertEquals(fileContent.getThreshold(), 10);
        assertEquals(fileContent.getTerms().size(), 2);

        Term firstTerm = fileContent.getTerms().get(0);
        assertEquals(firstTerm.getTerm(), "nigger");
        assertEquals(firstTerm.getScore(), 10);
        assertEquals(firstTerm.getReasons().size(), 1);
        assertTrue(firstTerm.getReasons().contains("racist"));

        Term secondTerm = fileContent.getTerms().get(0);
        assertEquals(secondTerm.getTerm(), "deport mexicans");
        assertEquals(secondTerm.getScore(), -1000);
        assertEquals(secondTerm.getReasons().size(), 2);
        assertTrue(secondTerm.getReasons().contains("racist"));
        assertTrue(secondTerm.getReasons().contains("fearmongering"));
    }
}
