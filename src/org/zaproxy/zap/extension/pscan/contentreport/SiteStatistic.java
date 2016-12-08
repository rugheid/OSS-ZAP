package org.zaproxy.zap.extension.pscan.contentreport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.URI;
import org.parosproxy.paros.network.HttpMessage;

public class SiteStatistic {
	
	public Statistic imageHeight;
	public Statistic imageWidth;
	public Statistic imageFileSize;
	public PercentageStatistic imageType;
	
	
	SiteStatistic() {
		this.imageHeight = new Statistic("height");
		this.imageWidth = new Statistic("width");
		this.imageFileSize = new Statistic("file size");
		this.imageType = new PercentageStatistic();
	}
	
	public void addEntry(HttpMessage msg) throws IOException {
		URI uri = msg.getRequestHeader().getURI();
		String extension = uri.toString().substring(uri.toString().lastIndexOf(".") + 1);
		BufferedImage image = imageFromBytes(msg.getResponseBody().getBytes());
		this.imageHeight.addEntry(image.getHeight(), uri);
		this.imageWidth.addEntry(image.getWidth(), uri);
		this.imageFileSize.addEntry(msg.getResponseBody().getBytes().length, uri);
		this.imageType.addEntry(extension);
	}

    static BufferedImage imageFromBytes(byte[] bytes) throws IOException {
        InputStream in = new ByteArrayInputStream(bytes);
        return ImageIO.read(in);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.imageHeight.toString()).append("\n");
		sb.append(this.imageWidth.toString()).append("\n");
		sb.append(this.imageFileSize.toString()).append("\n");
		sb.append(this.imageType.toString());
		return sb.toString();
	}
}
