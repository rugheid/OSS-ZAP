package org.zaproxy.zap.extension.pscan.contentreport;

import java.util.HashMap;

public class PercentageStatistic {
	
	private HashMap<String, Integer> nbExtension;
	private int totalImages;
	
	PercentageStatistic() {
		nbExtension = new HashMap<>();
	}
	
	public void addEntry(String extension) {
		totalImages++;
		if (! this.nbExtension.containsKey(extension)) {
			this.nbExtension.put(extension, 1);
		} else {
			this.nbExtension.replace(extension, this.nbExtension.get(extension) + 1);
		}
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