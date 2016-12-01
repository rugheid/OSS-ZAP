package org.parosproxy.paros.extension.filter.imageFilterActions;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;

public class SizeBasedEnhancement extends ImageFilterAction {

    private int allowedSize;

    {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/resource/oss/MagicNumbers.properties"));
            this.allowedSize = Integer.parseInt(properties.getProperty("SizeBasedEnhancement"));
        } catch (IOException e) {
            Logger.getLogger(SizeBasedEnhancement.class).error(e.getMessage(), e);
        }
    }

    private static BufferedImage toGrayscale(BufferedImage img) {
        // get image width and height
        int width = img.getWidth();
        int height = img.getHeight();

        // convert to grayscale
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(img.getRGB(x,y));

                int alpha = color.getAlpha();
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int avg = (red + green + blue) / 3;

                Color newColor = new Color(avg, avg, avg, alpha);

                img.setRGB(x, y, newColor.getRGB());
            }
        }
        return img;
    }

    @Override
    public void onHttpResponseReceive(HttpMessage msg) {
        if (msg.getResponseHeader().isEmpty() || !msg.getResponseHeader().isImage() ||
                msg.getResponseBody().length() < this.allowedSize * 1024)
            return;

        try {
            byte[] inputBytes = msg.getResponseBody().getBytes();
            BufferedImage input = imageFromBytes(inputBytes);
            byte[] resultBytes = bytesFromImage(toGrayscale(input));
            msg.setResponseBody(resultBytes);
            msg.getResponseHeader().setContentLength(resultBytes.length);
        } catch (IOException e) {
            Logger.getLogger(SizeBasedEnhancement.class).error(e.getMessage(), e);
        }
    }

}