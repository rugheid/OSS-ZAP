package org.parosproxy.paros.extension.filter.imageFilterActions;

import org.junit.Test;
import org.parosproxy.paros.network.HttpMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ImageFilterActionTest {

    private static final Path BASE_DIR_TEST_FILES = Paths.get("test/resources/org/parosproxy/paros/extension/filter/imageFilterActions");

    @Test
    public void testLoadActionWithNameSuccessful() {
        ImageFilterAction action = ImageFilterAction.loadActionWithName("Flip");
        assertNotNull(action);
        assertEquals(action.getClass(), Flip.class);
    }

    @Test
    public void testLoadActionWithNameUnsuccessful() {
        ImageFilterAction action = ImageFilterAction.loadActionWithName("");
        assertNull(action);
    }

    @Test
    public void testLoadActionWithNameNull() {
        ImageFilterAction action = ImageFilterAction.loadActionWithName(null);
        assertNull(action);
    }

    @Test
    public void testEnabled() {
        ImageFilterAction action = new ImageFilterAction() {
            @Override
            public void onHttpResponseReceive(HttpMessage msg) {}
        };

        assertFalse(action.isEnabled());

        action.setEnabled(true);
        assertTrue(action.isEnabled());

        action.setEnabled(false);
        assertFalse(action.isEnabled());
    }

    @Test
    public void testImageConversions() throws IOException {
        BufferedImage image = ImageIO.read(BASE_DIR_TEST_FILES.resolve("lena_color.gif").toFile());
        byte[] bytes = ImageFilterAction.bytesFromImage(image);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
        assertTrue(compareImages(image, ImageFilterAction.imageFromBytes(bytes)));
    }

    /**
     * Compares two images pixel by pixel.
     *
     * @param imgA the first image.
     * @param imgB the second image.
     * @return whether the images are both the same or not.
     */
    private static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
        // The images must be the same size.
        if (imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
            int width = imgA.getWidth();
            int height = imgA.getHeight();

            // Loop over every pixel.
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Compare the pixels for equality.
                    if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }
}
