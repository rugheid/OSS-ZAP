package org.parosproxy.paros.extension.filter.imageFilterActions;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;

public class Flip extends ImageFilterAction {

    private static BufferedImage flipImage(BufferedImage img) {
        int height = img.getHeight();
        for (int y = 0; y < img.getHeight() >> 1; ++y) {
            for (int x = 0; x < img.getWidth(); ++x) {
                int botY = height - y - 1;
                int top = img.getRGB(x, y);
                int bot = img.getRGB(x, botY);
                img.setRGB(x, y, bot);
                img.setRGB(x, botY, top);
            }
        }
        return img;
    }

    @Override
    public void onHttpResponseReceive(HttpMessage msg) {
        if (msg.getResponseHeader().isEmpty() || !msg.getResponseHeader().isImage() || msg.getResponseBody().length() == 0)
            return;

        try {
            byte[] inputBytes = msg.getResponseBody().getBytes();
            BufferedImage input = imageFromBytes(inputBytes);
            if (input == null) return;
            byte[] resultBytes = bytesFromImage(flipImage(input));
            msg.setResponseBody(resultBytes);
            msg.getResponseHeader().setContentLength(resultBytes.length);
        } catch (IOException e) {
            Logger.getLogger(Flip.class).error(e.getMessage(), e);
        }
    }
}
