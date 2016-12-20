package org.zaproxy.zap.extension.pscan.contentreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.parosproxy.paros.core.scanner.Category;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpMessage;

/**
 * Unit test for {@link ContentReportScanner}.
 */
public class ContentReportScannerUnitTest {

	private ContentReportScanner scanner;
	private HttpMessage msg;
	
	@Before
	public void setUp() throws Exception {
		scanner = new ContentReportScanner();
		msg = createImageMessageWith(12, 100);
	}

	@Test
	public void shouldApplyToHistoryTypes() {
		
		Boolean result = scanner.appliesToHistoryType(HistoryReference.TYPE_ZAP_USER) &&
				scanner.appliesToHistoryType(HistoryReference.TYPE_SPIDER) &&
				scanner.appliesToHistoryType(HistoryReference.TYPE_SPIDER_AJAX) &&
				scanner.appliesToHistoryType(HistoryReference.TYPE_HIDDEN) &&
				scanner.appliesToHistoryType(HistoryReference.TYPE_PROXIED);
		assertTrue(result);
	}

	@Test
	public void shouldHaveCategoryType() {
		assertEquals(Category.INFO_GATHER, scanner.getCategory());
	}
	@Test(expected = NullPointerException.class)
	public void shouldFailToAddEntryWithoutParent() {
		// Given
		scanner.setParent(null);
		// When
		scanner.scanHttpResponseReceive(msg, 0, null);
		// Then = NullPointerException.
	}
	
	

    private static HttpMessage createImageMessageWith(int height, int width) {
    	BufferedImage bi= new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] image = {1, 2};
    	try {
			ImageIO.write(bi, "jpg", baos);
	    	baos.flush();
	    	image = baos.toByteArray();
	    	baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return createMessageWith("image/jpg", image);
    }

    private static HttpMessage createMessageWith(String contentType, byte[] image) {
        return createMessageWith("200 OK", contentType, image);
    }

    private static HttpMessage createMessageWith(String statusCodeMessage, String contentType, byte[] image) {
        HttpMessage message = new HttpMessage();
        try {
            message.setRequestHeader("GET / HTTP/1.1\r\nHost: example.com\r\n");
            message.setResponseHeader(
                    "HTTP/1.1 " + statusCodeMessage + "\r\n" + "Content-Type: " + contentType + "; charset=UTF-8\r\n"
                            + "Content-Length: " + image.length);
            message.setResponseBody(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return message;
    }

}

