package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.junit.Test;
import org.parosproxy.paros.network.HttpMessage;

import static org.junit.Assert.*;

public class ImageFilterActionTest {

    @Test
    public void testLoadActionWithNameSuccessful() {
        ImageFilterAction action = ImageFilterAction.loadActionWithName("Flip");
        assertNotNull(action);
        assertEquals(action.getClass(), Flip.class);
    }

    @Test
    public void testLoadActionWithNameUnsuccessful() {
        ImageFilterAction action = ImageFilterAction.loadActionWithName("");
        assertNull(action);
    }

    @Test
    public void testLoadActionWithNameNull() {
        ImageFilterAction action = ImageFilterAction.loadActionWithName(null);
        assertNull(action);
    }

    @Test
    public void testEnabled() {
        ImageFilterAction action = new ImageFilterAction() {
            @Override
            public void onHttpResponseReceive(HttpMessage msg) {}
        };

        assertFalse(action.isEnabled());

        action.setEnabled(true);
        assertTrue(action.isEnabled());

        action.setEnabled(false);
        assertFalse(action.isEnabled());
    }
}
