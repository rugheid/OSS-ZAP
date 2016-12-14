package org.zaproxy.zap.extension.pscan.contentreport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.parosproxy.paros.network.HttpMessage;

public class ImagePropertyStatistic implements Statistic {

    String name;
    Function<BufferedImage, Integer> computeStatistic;
    List<Integer> data;

    public ImagePropertyStatistic(String name, Function<BufferedImage, Integer> computeStatistic) {
        this.name = name;
        this.computeStatistic = computeStatistic;
    }

    @Override
    public void addEntry(HttpMessage msg) {
        BufferedImage img;
        if ((img = imageFromBytes(msg.getResponseBody().getBytes())) != null) {
            Integer result = this.computeStatistic.apply(img);
            // TODO save URI as well
            data.add(result);
        }
        
    }

    @Override
    public String toReportString() {
        // TODO serious report string
        return name + ": " + data.stream().mapToInt(Integer::intValue).sum();
    }

    static BufferedImage imageFromBytes(byte[] bytes) {
        try {
            InputStream in = new ByteArrayInputStream(bytes);
            return ImageIO.read(in);
        } catch (IOException e) {
            return null;
        }
    }
    
}
