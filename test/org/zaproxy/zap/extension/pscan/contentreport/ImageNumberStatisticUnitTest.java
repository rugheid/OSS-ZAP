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
 * Unit test for {@link ImageNumberStatisticUnitTest}.
 */
public class ImageNumberStatisticUnitTest {

	private ImageNumberStatisticTester statistic;
	private HttpMessage msg;
	
	@Before
	public void setUp() throws Exception {
		statistic = new ImageNumberStatisticTester();
		msg = createImageMessageWith(12, 100);
	}

	@Test(expected = NullPointerException.class)
	public void shouldFailOnNotDecodableMessage() {
		// Given
		byte [] randomData = {1, 3, 3, 7};
		msg.setResponseBody(randomData);
		// When
		statistic.update(msg);
		// Then = IOException
	}
	
	@Test
	public void ShoudReturnZeroWhenNoNumbersAddedAverage() {
		assertEquals(statistic.getAverage(), 0);
		
	}
	
	@Test
	public void ShouldCalculateCorrectAverage() {
		Random rand = new Random();
		
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i < 4; i++) {
		    list.add(rand.nextInt(100) + 1);
		}
		
		list.stream().forEach(i -> statistic.update(createImageMessageWith(i.intValue(), 50)));
		assertEquals(statistic.getAverage(), list.stream().mapToInt(a -> a).sum() / 3);
		
	}
	
	@Test
	public void ShoudReturnNegInfWhenNoNumbersAddedMaximum() {
		assertEquals(statistic.getMaximum(), Integer.MIN_VALUE);
		
	}
	
	@Test
	public void ShouldCalculateCorrectMaximum() {
		Random rand = new Random();
		
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i < 10; i++) {
		    list.add(rand.nextInt(100) + 1);
		}
		
		list.stream().forEach(i -> statistic.update(createImageMessageWith(i.intValue(), 50)));
		assertEquals(statistic.getMaximum(), list.stream().mapToInt(a -> a).max().getAsInt());
		
	}
	
	@Test
	public void ShoudReturnPosInfWhenNoNumbersAddedMinimum() {
		assertEquals(Integer.MAX_VALUE, statistic.getMinimum());
		
	}
	
	@Test
	public void ShouldCalculateCorrectMinimum() {
		Random rand = new Random();
		
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i < 10; i++) {
		    list.add(rand.nextInt(100) + 1);
		}
		
		list.stream().forEach(i -> statistic.update(createImageMessageWith(i.intValue(), 50)));
		assertEquals(statistic.getMinimum(), list.stream().mapToInt(a -> a).min().getAsInt());
		
	}
    
	@Test
	public void ShoudReturnZeroWhenNoNumbersAddedMedian() {
		assertEquals(0, statistic.getMedian());
		
	}
	
	@Test
	public void ShouldCalculateCorrectMedian() {
		Random rand = new Random();
		
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i < 3; i++) {
		    list.add(rand.nextInt(100) + 1);
		}
		
		list.stream().forEach(i -> statistic.update(createImageMessageWith(i.intValue(), 50)));
		int[] sortedList = list.stream().mapToInt(a->a).toArray();
		Arrays.sort(sortedList);
		assertEquals(statistic.getMedian(), sortedList[list.size()/2]);
		
	}
	
	private class ImageNumberStatisticTester extends ImageNumberStatistic {

		ImageNumberStatisticTester() {
			super("test");
		}

		@Override
		int parseMessage(HttpMessage msg) throws IOException {
			BufferedImage image = imageFromBytes(msg.getResponseBody().getBytes());
			return image.getWidth();
		}
		
		
	}

    private static HttpMessage createImageMessageWith(int height, int width) {
    	BufferedImage bi= new BufferedImage(height,width,BufferedImage.TYPE_BYTE_GRAY);
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

