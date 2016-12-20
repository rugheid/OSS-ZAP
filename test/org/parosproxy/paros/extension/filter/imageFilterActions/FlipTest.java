package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.junit.Before;
import org.junit.Test;
import org.parosproxy.paros.extension.filter.FilterTestUtils;
import org.parosproxy.paros.network.HttpMessage;

import static org.junit.Assert.*;

public class FlipTest {

    private Flip flip;

    @Before
    public void setup() {
        flip = new Flip();
    }

    @Test
    public void testDontFlipEmptyMessage() {
        HttpMessage message = new HttpMessage();
        HttpMessage original = new HttpMessage();
        flip.onHttpResponseReceive(message);
        assertEquals(message, original);
        assertArrayEquals(message.getResponseBody().getBytes(), original.getResponseBody().getBytes());
    }

    @Test
    public void testDontFlipHTMLPage() {
        HttpMessage message = FilterTestUtils.createMessageWith("simple.html");
        HttpMessage original = FilterTestUtils.createMessageWith("simple.html");
        flip.onHttpResponseReceive(message);
        assertEquals(message, original);
        assertArrayEquals(message.getResponseBody().getBytes(), original.getResponseBody().getBytes());
    }

    @Test
    public void testFlipImage() {
        HttpMessage message = FilterTestUtils.createMessageFromImage("trump.jpg", "jpg", "www.trump.me");
        HttpMessage original = FilterTestUtils.createMessageFromImage("trump.jpg", "jpg", "www.trump.me");
        flip.onHttpResponseReceive(message);
        assertFalse(message.getResponseBody().getBytes().equals(original.getResponseBody().getBytes()));
        assertEquals(message.getResponseHeader().getContentLength(), message.getResponseBody().getBytes().length);
    }
}
