package org.parosproxy.paros.extension.filter;

import org.parosproxy.paros.network.HttpMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilterTestUtils {

    private static final String FORM_METHOD_TOKEN = "%%METHOD%%";
    private static final String FORM_ACTION_TOKEN = "%%ACTION%%";
    private static final String BASE_HTML_TOKEN = "%%BASE_HTML%%";

    private static final Path BASE_DIR_HTML_FILES = Paths.get("test/resources/org/parosproxy/paros/extension/filter/html");

    public static String readFile(Path file) throws IOException {
        StringBuilder strBuilder = new StringBuilder();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            strBuilder.append(line).append('\n');
        }
        return strBuilder.toString();
    }

    public static HttpMessage createMessageWith(String filename) {
        return createMessageWith(null, filename);
    }

    public static HttpMessage createMessageWith(String formMethod, String filename) {
        return createMessageWith(formMethod, filename, null, null, "/");
    }

    public static HttpMessage createMessageWith(String formMethod, String filename, String formAction, String baseHtml) {
        return createMessageWith(formMethod, filename, formAction, baseHtml, "/");
    }

    public static HttpMessage createMessageWith(
            String formMethod,
            String filename,
            String formAction,
            String baseHtml,
            String requestUri) {
        HttpMessage message = new HttpMessage();
        try {
            String fileContents = readFile(BASE_DIR_HTML_FILES.resolve(filename));
            if (formMethod != null) {
                fileContents = fileContents.replace(FORM_METHOD_TOKEN, formMethod);
            }
            if (formAction != null) {
                fileContents = fileContents.replace(FORM_ACTION_TOKEN, formAction);
            }
            if (baseHtml != null) {
                fileContents = fileContents.replace(BASE_HTML_TOKEN, baseHtml);
            }
            message.setRequestHeader("GET " + requestUri + " HTTP/1.1\r\nHost: example.com\r\n");
            message.setResponseHeader(
                    "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html; charset=UTF-8\r\n" + "Content-Length: "
                            + fileContents.length());
            message.setResponseBody(fileContents);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    public static HttpMessage createMessageFromImage(String fileName, String imageType, String requestUri) {
        HttpMessage message = new HttpMessage();
        try {
            BufferedImage img = ImageIO.read(BASE_DIR_HTML_FILES.resolve(fileName).toFile());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, imageType, baos);
            baos.flush();
            byte[] bytes = baos.toByteArray();
            baos.close();
            message.setRequestHeader("GET " + requestUri + " HTTP/1.1\r\nHost: example.com\r\n");
            message.setResponseHeader(
                    "HTTP/1.1 200 OK\r\n" + "Content-Type: image/" + imageType + "; charset=UTF-8\r\n" + "Content-Length: "
                            + bytes.length);
            message.setResponseBody(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return message;
    }
}
