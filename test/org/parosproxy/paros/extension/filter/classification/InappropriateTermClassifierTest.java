package org.parosproxy.paros.extension.filter.classification;

import org.junit.Before;
import org.junit.Test;
import org.parosproxy.paros.extension.filter.FilterTestUtils;
import org.parosproxy.paros.extension.filter.classifier.ContentClassifier.Classification;
import org.parosproxy.paros.extension.filter.classifier.InappropriateTermClassifier;
import org.parosproxy.paros.extension.filter.classifier.InappropriateTermParser.Term;
import org.parosproxy.paros.extension.filter.classifier.InappropriateTermParser.InappropriateTermFileContent;
import org.parosproxy.paros.network.HttpMessage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class InappropriateTermClassifierTest {

    private InappropriateTermClassifier classifier;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        classifier = new InappropriateTermClassifier();

        Set<String> firstReasons = new HashSet<>();
        firstReasons.add("racist");
        Term firstTerm = new Term("nigger", 10, firstReasons);

        Set<String> secondReasons = new HashSet<>();
        secondReasons.add("racist");
        Term secondTerm = new Term("trump", 4, secondReasons);

        List<Term> terms = new ArrayList<>();
        terms.add(firstTerm);
        terms.add(secondTerm);
        InappropriateTermFileContent fileContent = new InappropriateTermFileContent(10, terms);

        Field fileContentField = InappropriateTermClassifier.class.getDeclaredField("fileContent");
        fileContentField.setAccessible(true);
        fileContentField.set(classifier, fileContent);
    }

    @Test
    public void testCanClassify() {
        HttpMessage message = new HttpMessage();
        assertFalse(classifier.canClassify(message));

        message = FilterTestUtils.createMessageFromImage("trump.jpg", "jpg", "/trump");
        assertFalse(classifier.canClassify(message));

        message = FilterTestUtils.createMessageWith("simple.html");
        assertTrue(classifier.canClassify(message));
    }

    @Test
    public void testClassifyFalse() {
        HttpMessage message = FilterTestUtils.createMessageWith("simple.html");
        Classification classification = classifier.classify(message);
        assertFalse(classification.isClassifiedInappropriate());
    }

    @Test
    public void testClassifyTrue() {
        HttpMessage message = FilterTestUtils.createMessageWith("inappropriate.html");
        Classification classification = classifier.classify(message);
        assertTrue(classification.isClassifiedInappropriate());
        assertFalse(classification.getReasons().isEmpty());
    }
}
