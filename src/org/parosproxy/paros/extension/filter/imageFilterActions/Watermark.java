package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.apache.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class Watermark extends ImageFilterAction {

    private static final URL WATERMARK_FILE_URL = Watermark.class.getResource("/resource/oss/trump-head.png");

    private static void addImageWatermark(BufferedImage watermarkImage, BufferedImage image) {
        // initializes necessary graphic properties
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
        g2d.setComposite(alphaChannel);

        // calculates the coordinate where the image is painted
        int topLeftX = (image.getWidth() - watermarkImage.getWidth()) / 2;
        int topLeftY = (image.getHeight() - watermarkImage.getHeight()) / 2;
        if (topLeftX <= 0 || topLeftY <= 0)
            return; // Image is too small

        // paints the image watermark
        g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);
        g2d.dispose();
    }

    @Override
    public void onHttpResponseReceive(HttpMessage msg) {
        if (msg.getResponseHeader().isEmpty() || !msg.getResponseHeader().isImage()) return;

        try {
            byte[] inputBytes = msg.getResponseBody().getBytes();
            BufferedImage input = imageFromBytes(inputBytes);
            BufferedImage watermarkImage = ImageIO.read(WATERMARK_FILE_URL);
            if (input == null || watermarkImage == null) return;

            addImageWatermark(watermarkImage, input);

            byte[] resultBytes = bytesFromImage(input);
            msg.setResponseBody(resultBytes);
            msg.getResponseHeader().setContentLength(resultBytes.length);
        } catch (IOException e) {
            Logger.getLogger(Watermark.class).error(e.getMessage(), e);
        }
    }

}
