package org.parosproxy.paros.extension.filter.imageFilterActions;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;

public abstract class ImageFilterAction {

    private Boolean enabled = false;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public abstract void onHttpResponseReceive(HttpMessage msg);

    public static ImageFilterAction loadActionWithName(String name) {
        try {
            Class<?> clazz = Class.forName("org.parosproxy.paros.extension.filter.imageFilterActions." + name);
            return (ImageFilterAction) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Logger.getLogger(ImageFilterAction.class).error(e.getMessage(), e);
            return null;
        }
    }

    static BufferedImage imageFromBytes(byte[] bytes) throws IOException {
        InputStream in = new ByteArrayInputStream(bytes);
        return ImageIO.read(in);
    }

    static byte[] bytesFromImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }
}
