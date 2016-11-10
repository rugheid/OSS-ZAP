package org.parosproxy.paros.extension.filter.imageFilterActions;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpResponseHeader;

public class SizeBasedEnhancement extends ImageFilterAction {

	public static BufferedImage toGrayscale(BufferedImage img) {
		 //get image width and height
	    int width = img.getWidth();
	    int height = img.getHeight();

	    //convert to grayscale
	    for(int y = 0; y < height; y++){
	      for(int x = 0; x < width; x++){
	        int p = img.getRGB(x,y);

	        int a = (p>>24)&0xff;
	        int r = (p>>16)&0xff;
	        int g = (p>>8)&0xff;
	        int b = p&0xff;

	        //calculate average
	        int avg = (r+g+b)/3;

	        //replace RGB value with avg
	        p = (a<<24) | (avg<<16) | (avg<<8) | avg;

	        img.setRGB(x, y, p);
	      }
	    }
	    return img;
	}
	
    @Override
    public void onHttpResponseReceive(HttpMessage msg) {
        if (!msg.getResponseHeader().isImage()) return;
        if (msg.getResponseBody().length() < 100 * 1024) return;

        try {
            byte[] byteImage = msg.getResponseBody().getBytes();
            InputStream in = new ByteArrayInputStream(byteImage);
            BufferedImage bufferedImage = ImageIO.read(in);
            if (bufferedImage == null) return;
            BufferedImage resultImage = SizeBasedEnhancement.toGrayscale(bufferedImage);            
           
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resultImage, "png", baos);
            baos.flush();
            byte[] resultBytes = baos.toByteArray();
			baos.close();
	        msg.setResponseBody(resultBytes);
	        msg.getResponseHeader().setContentLength(resultBytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
