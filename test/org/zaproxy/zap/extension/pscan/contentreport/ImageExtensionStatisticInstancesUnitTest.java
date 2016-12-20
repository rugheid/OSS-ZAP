package org.zaproxy.zap.extension.pscan.contentreport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.parosproxy.paros.network.HttpMessage;

/**
 * Unit test for {@link ImageExtensionStatisticInstancesUnitTest}.
 */
public class ImageExtensionStatisticInstancesUnitTest {

	private HttpMessage msg;
	
	@Before
	public void setUp() throws Exception {
		msg = createImageMessageWith(12, 100);
	}

	
	@Test
	public void shouldReturnImageHeight() {
		ImageHeightStatistic statistic = new ImageHeightStatistic();
		statistic.update(msg);
		assertEquals(100, statistic.getMaximum());
	}
	
	@Test
	public void shouldReturnImageWidth() {
		ImageWidthStatistic statistic = new ImageWidthStatistic();
		statistic.update(msg);
		assertEquals(12, statistic.getMaximum());
	}
	
	@Test
	public void shouldReturnImageSize() {
		ImageSizeStatistic statistic = new ImageSizeStatistic();
		statistic.update(msg);
		assertEquals(msg.getResponseBody().getBytes().length, statistic.getMaximum());
	}

	
    private static HttpMessage createImageMessageWith(int width, int height) {
    	BufferedImage bi= new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
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
        return createMessageWith("200 OK", "image/JPEG", image);
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

