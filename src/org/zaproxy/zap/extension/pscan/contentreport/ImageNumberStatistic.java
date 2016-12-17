package org.zaproxy.zap.extension.pscan.contentreport;

import org.apache.commons.httpclient.URI;
import org.apache.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Function;

public class ImageNumberStatistic implements Statistic {
	
	public final String name;
	private ArrayList<Integer> data = new ArrayList<>();
	private int maximum = Integer.MIN_VALUE;
	private URI maxURI;
	private int minimum = Integer.MAX_VALUE;
	private URI minURI;
    private final Function<HttpMessage, Integer> parseMessage;

	ImageNumberStatistic(String name, Function<HttpMessage, Integer> fn) {
		this.name = name;
		this.parseMessage = fn;
	}

	public void addEntry(HttpMessage msg) {
		int entry;
		try {
			entry = this.parseMessage.apply(msg);
		} catch (NullPointerException e) {
            Logger.getLogger(ImageNumberStatistic.class).info("Failed to decode image.");
			return;
		}
		data.add(entry);
		if (entry > maximum) {
			maximum = entry;
			maxURI = msg.getRequestHeader().getURI();
		}
		if (entry < minimum) {
			minimum = entry;
			minURI = msg.getRequestHeader().getURI();
		}
	}

	public int getMaximum() {
		return maximum;
	}
	
	public URI getMaximumURI() {
		return maxURI;
	}
	
	public int getMinimum() {
		return minimum;
	}
	
	public URI getMinimumURI() {
		return minURI;
	}
	
	public int getMedian() {
		if (!(data.size() > 0)) {
			return 0;
		}
		return data.get(data.size()/2);
	}
	
	public int getAverage() {
		if (!(data.size() > 0)) {
			return 0;
		}
		return data.stream().mapToInt(a -> a).sum() / data.size();
	}

    static BufferedImage imageFromBytes(byte[] bytes) {
		try{
			InputStream in = new ByteArrayInputStream(bytes);
			return ImageIO.read(in);
		} catch (IOException e) {
            return null;
		}
	}

	public String toReportString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Maximum ").append(name).append(": ");
		sb.append(this.getMaximum()).append("\n");
		sb.append("URI Maximum ").append(name).append(": ");
		sb.append(this.getMaximumURI()).append("\n");
		sb.append("Minimum ").append(name).append(": ");
		sb.append(this.getMinimum()).append("\n");
		sb.append("URI Minimum ").append(name).append(": ");
		sb.append(this.getMinimumURI()).append("\n");
		sb.append("Average ").append(name).append(": ");
		sb.append(this.getAverage()).append("\n");
		sb.append("Median ").append(name).append(": ");
		sb.append(this.getMedian()).append("\n");
		return sb.toString();
	}
}