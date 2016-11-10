package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.parosproxy.paros.network.HttpMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class Watermark extends ImageFilterAction {

    @Override
    public void onHttpResponseReceive(HttpMessage msg) {
        if (!msg.getResponseHeader().isImage()) return;

        try {
            BufferedImage image = imageFromBytes(msg.getResponseBody().getBytes());

            URL watermarkFile = getClass().getResource("/resource/oss/trump-head.png");
            addImageWatermark(watermarkFile, image);

            byte[] bytes = bytesFromImage(image);
            msg.setResponseBody(bytes);
            msg.getResponseHeader().setContentLength(bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addImageWatermark(URL watermarkImageFile, BufferedImage image) {
        try {
            BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

            // initializes necessary graphic properties
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
            g2d.setComposite(alphaChannel);

            // calculates the coordinate where the image is painted
            int topLeftX = (image.getWidth() - watermarkImage.getWidth()) / 2;
            int topLeftY = (image.getHeight() - watermarkImage.getHeight()) / 2;

            // paints the image watermark
            g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);
            g2d.dispose();

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private static BufferedImage imageFromBytes(byte[] bytes) throws IOException {
        InputStream in = new ByteArrayInputStream(bytes);
        return ImageIO.read(in);
    }

    private static byte[] bytesFromImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }
}
