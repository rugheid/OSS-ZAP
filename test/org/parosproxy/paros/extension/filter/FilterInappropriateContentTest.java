package org.parosproxy.paros.extension.filter;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FilterInappropriateContentTest {

    private FilterInappropriateContent filter;

    @Before
    public void setup() {
        filter = new FilterInappropriateContent();
    }

    // FIXME: These two tests don't work because of the i18n (ResourceBundle)
    @Test
    public void testName() {
        String name = filter.getName();
        assertTrue(name != null && !name.isEmpty());
    }

    @Test
    public void testUniqueID() {
        FilterFactory factory = new FilterFactory();
        factory.loadAllFilter();
        List<Filter> filters = factory.getAllFilter();
        int id = filter.getId();
        for (Filter filter: filters) {
            assertNotEquals(filter.getId(), id);
        }
    }
}
