package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.junit.Before;
import org.junit.Test;
import org.parosproxy.paros.extension.filter.FilterTestUtils;
import org.parosproxy.paros.network.HttpMessage;

import java.lang.reflect.Field;
import static org.junit.Assert.*;

public class SizeBasedEnhancementTest {

    private SizeBasedEnhancement sizeBasedEnhancement;

    @Before
    public void setup() throws IllegalAccessException, NoSuchFieldException {
        sizeBasedEnhancement = new SizeBasedEnhancement();
        Field allowedSizeField = SizeBasedEnhancement.class.getDeclaredField("allowedSize");
        allowedSizeField.setAccessible(true);
        allowedSizeField.setInt(sizeBasedEnhancement, 20);
    }

    @Test
    public void testDontFilterEmptyMessage() {
        HttpMessage message = new HttpMessage();
        HttpMessage original = new HttpMessage();
        sizeBasedEnhancement.onHttpResponseReceive(message);
        assertEquals(message, original);
        assertArrayEquals(message.getResponseBody().getBytes(), original.getResponseBody().getBytes());
    }

    @Test
    public void testDontFilterHTMLPage() {
        HttpMessage message = FilterTestUtils.createMessageWith("simple.html");
        HttpMessage original = FilterTestUtils.createMessageWith("simple.html");
        sizeBasedEnhancement.onHttpResponseReceive(message);
        assertEquals(message, original);
        assertArrayEquals(message.getResponseBody().getBytes(), original.getResponseBody().getBytes());
    }

    @Test
    public void testDontFilterTooSmallImage() {
        HttpMessage message = FilterTestUtils.createMessageFromImage("trump.jpg", "jpg", "www.trump.me");
        HttpMessage original = FilterTestUtils.createMessageFromImage("trump.jpg", "jpg", "www.trump.me");
        sizeBasedEnhancement.onHttpResponseReceive(message);
        assertEquals(message, original);
        assertArrayEquals(message.getResponseBody().getBytes(), original.getResponseBody().getBytes());
    }

    @Test
    public void testFilterBigImage() {
        HttpMessage message = FilterTestUtils.createMessageFromImage("wallpaper.jpg", "jpg", "www.wallpaper.com");
        HttpMessage original = FilterTestUtils.createMessageFromImage("wallpaper.jpg", "jpg", "www.wallpaper.com");
        sizeBasedEnhancement.onHttpResponseReceive(message);
        assertFalse(message.getResponseBody().getBytes().equals(original.getResponseBody().getBytes()));
    }
}
