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
 * Unit test for {@link ImageExtensionStatisticUnitTest}.
 */
public class ImageExtensionStatisticUnitTest {

	private ImageExtensionStatistic statistic;
	
	@Before
	public void setUp() throws Exception {
		statistic = new ImageExtensionStatistic();
	}

	@Test
	public void shouldReturnCorrectStatistics() {
		HttpMessage pngMsg = createImageMessageWith(12, 12, "png");
		HttpMessage jpgMsg = createImageMessageWith(12, 12, "JPEG");
		
		for(int i = 0; i < 7; i++) {
			statistic.update(pngMsg);
		}
		for(int i = 0; i < 3; i++) {
			statistic.update(jpgMsg);
		}
		assertEquals(70.0, statistic.getPercentage("png"), 0.0001);
		assertEquals(30.0, statistic.getPercentage("JPEG"), 0.0001);
	}
	
	@Test
	public void shouldNotCreateEntryForBadImages() {
		HttpMessage pngMsg = createImageMessageWith(12, 12, "png");
		HttpMessage jpgMsg = createImageMessageWith(12, 12, "JPEG");
		HttpMessage badMsg = createMessageWith("200 OK", "image/lol", new byte[] {1, 2, 3, 4});
		
		for(int i = 0; i < 5; i++) {
			statistic.update(pngMsg);
		}
		for(int i = 0; i < 3; i++) {
			statistic.update(jpgMsg);
		}
		for(int i = 0; i < 2; i++) {
			statistic.update(badMsg);
		}
		assertEquals(62.5, statistic.getPercentage("png"), 0.0001);
		assertEquals(37.5, statistic.getPercentage("JPEG"), 0.0001);
	}
	
	@Test
	public void shouldReturnZeroForUnkownExtension() {
		assertEquals(statistic.getPercentage("jpg"), 0.0, 0.1);
	}

	@Test
	public void shouldReturnImageHeight() {
		ImageHeightStatistic statistic = new ImageHeightStatistic();
		
	}
	
    private static HttpMessage createImageMessageWith(int height, int width, String type) {
    	BufferedImage bi= new BufferedImage(height,width,BufferedImage.TYPE_BYTE_GRAY);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] image = {1, 2};
    	try {
			ImageIO.write(bi, type, baos);
	    	baos.flush();
	    	image = baos.toByteArray();
	    	baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return createMessageWith("200 OK", "image/"+type, image);
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

