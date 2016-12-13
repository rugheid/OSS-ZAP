package org.zaproxy.zap.extension.pscan.contentreport;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.parosproxy.paros.network.HttpMessage;

public class SiteStatistic {
	
	public NumberStatistic imageHeight;
	public NumberStatistic imageWidth;
	public NumberStatistic imageFileSize;
	public PercentageStatistic imageType;
	
	SiteStatistic() {
		this.imageHeight = new NumberStatistic("height");
		this.imageWidth = new NumberStatistic("width");
		this.imageFileSize = new NumberStatistic("file size");
		this.imageType = new PercentageStatistic();
	}
	
	public void addEntry(HttpMessage msg) throws IOException {
		String uri = msg.getRequestHeader().getURI().toString();
		BufferedImage image = imageFromBytes(msg.getResponseBody().getBytes());
		String extension = extensionOfImageFromBytes(msg.getResponseBody().getBytes());
		
		if (image == null || extension == null) {
			return;
		}
		
		this.imageHeight.addEntry(image.getHeight(), uri);
		this.imageWidth.addEntry(image.getWidth(), uri);
		this.imageFileSize.addEntry(msg.getResponseBody().getBytes().length, uri);
		this.imageType.addEntry(1, extension);
	}

	
    static BufferedImage imageFromBytes(byte[] bytes) throws IOException {
        InputStream in = new ByteArrayInputStream(bytes);
        return ImageIO.read(in);
	}
    
    static String extensionOfImageFromBytes(byte[] bytes) throws IOException{
    	String extension = null;
		ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes));
		Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
		if (readers.hasNext()) {
		    ImageReader read = readers.next();
		    extension = read.getFormatName();
		}
		return extension;
    }
	
	public String toReportString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.imageHeight.toReportString()).append("\n------------------------------------\n");
		sb.append(this.imageWidth.toReportString()).append("\n------------------------------------\n");
		sb.append(this.imageFileSize.toReportString()).append("\n------------------------------------\n");
		sb.append(this.imageType.toReportString());
		return sb.toString();
	}
}
