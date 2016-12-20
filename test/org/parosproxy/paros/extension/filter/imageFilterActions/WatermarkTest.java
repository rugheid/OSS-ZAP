package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.junit.Before;
import org.junit.Test;
import org.parosproxy.paros.extension.filter.FilterTestUtils;
import org.parosproxy.paros.network.HttpMessage;

import static org.junit.Assert.*;

public class WatermarkTest {

    private Watermark watermark;

    @Before
    public void setup() {
        watermark = new Watermark();
    }

    @Test
    public void testDontFilterEmptyMessage() {
        HttpMessage message = new HttpMessage();
        HttpMessage original = new HttpMessage();
        watermark.onHttpResponseReceive(message);
        assertEquals(message, original);
        assertArrayEquals(message.getResponseBody().getBytes(), original.getResponseBody().getBytes());
    }

    @Test
    public void testDontFilterHTMLPage() {
        HttpMessage message = FilterTestUtils.createMessageWith("simple.html");
        HttpMessage original = FilterTestUtils.createMessageWith("simple.html");
        watermark.onHttpResponseReceive(message);
        assertEquals(message, original);
        assertArrayEquals(message.getResponseBody().getBytes(), original.getResponseBody().getBytes());
    }

    @Test
    public void testFilterImage() {
        HttpMessage message = FilterTestUtils.createMessageFromImage("trump.jpg", "jpg", "/trump");
        HttpMessage original = FilterTestUtils.createMessageFromImage("trump.jpg", "jpg", "/trump");
        watermark.onHttpResponseReceive(message);
        assertFalse(message.getResponseBody().getBytes().equals(original.getResponseBody().getBytes()));
    }
}
