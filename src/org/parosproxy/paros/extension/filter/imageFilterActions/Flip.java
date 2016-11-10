package org.parosproxy.paros.extension.filter.imageFilterActions;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;

public class Flip extends ImageFilterAction {
	
	public static BufferedImage flipImage(BufferedImage img) {
        // https://examples.javacodegeeks.com/desktop-java/awt/image/flipping-a-buffered-image/
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -img.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
	}

    @Override
    public void onHttpResponseReceive(HttpMessage msg) {
        if (msg.getResponseHeader().isEmpty() || !msg.getResponseHeader().isImage() || msg.getResponseBody().length() == 0) {
            return;
        }
        
        try {
            byte[] byteImage = msg.getResponseBody().getBytes();
            InputStream in = new ByteArrayInputStream(byteImage);
            BufferedImage bufferedImage = ImageIO.read(in);
            if (bufferedImage == null) return;
            BufferedImage resultImage = Flip.flipImage(bufferedImage);            
           
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resultImage, "png", baos);
            baos.flush();
            byte[] resultBytes = baos.toByteArray();
			baos.close();
	        msg.setResponseBody(resultBytes);
	        msg.getResponseHeader().setContentLength(resultBytes.length);
		} catch (IOException e) {
        	Logger.getLogger(Flip.class).error(e.getMessage(), e);
		}
    }
}
