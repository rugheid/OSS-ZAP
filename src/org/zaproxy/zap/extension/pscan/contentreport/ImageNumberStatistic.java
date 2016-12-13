package org.zaproxy.zap.extension.pscan.contentreport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.URI;
import org.apache.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;

public abstract class ImageNumberStatistic implements Statistic {
	
	public static final String name = "statistic";
	private ArrayList<Integer> data = new ArrayList<Integer>();
	private int maximum = Integer.MIN_VALUE;
	private URI maxURI;
	private int minimum = Integer.MAX_VALUE;
	private URI minURI;

	
	abstract int parseMessage(HttpMessage msg) throws IOException;

	public void addEntry(HttpMessage msg) {
		int entry;
		try {
			entry = parseMessage(msg);
		} catch (IOException e) {
			Logger.getLogger(ImageNumberStatistic.class).error("Image could not be decoded.", e);
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
	
	
    static BufferedImage imageFromBytes(byte[] bytes) throws IOException {
        InputStream in = new ByteArrayInputStream(bytes);
        return ImageIO.read(in);
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