package org.zaproxy.zap.extension.pscan.contentreport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;

public class ExtensionPercentageStatistic implements Statistic {
	
	private HashMap<String, Integer> nbExtension;
	private int totalImages;
	
	ExtensionPercentageStatistic() {
		nbExtension = new HashMap<>();
	}
	
	public void addEntry(HttpMessage msg) {
		String extension;
		try {
			extension = extensionOfImageFromBytes(msg.getResponseBody().getBytes());
		} catch (IOException e) {
			Logger.getLogger(ExtensionPercentageStatistic.class).error("Extension could not be decoded.", e);
			return;
		}
		totalImages = totalImages + 1;
		if (! this.nbExtension.containsKey(extension)) {
			this.nbExtension.put(extension, 1);
		} else {
			this.nbExtension.replace(extension, this.nbExtension.get(extension) + 1);
		}
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
		StringBuilder sb =  new StringBuilder();
		sb.append("Number of images: ").append(this.totalImages).append("\n");
		sb.append("Image extension statistics:\n");
		for (String extension: this.nbExtension.keySet()){
			sb.append(extension).append(": ").append(String.format("%.2f", getPercentage(extension))).append("%\n");
		}
		return sb.toString();
	}
	
	private double getPercentage(String extension) {
		int occurences = this.nbExtension.get(extension);
		return (double) occurences / (double) this.totalImages * 100;
	}
	
}