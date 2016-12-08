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
	
	@Override
	public String toString() {
		StringBuilder sb =  new StringBuilder();
		sb.append("Image extention statistics:\n\r");
		for (String extension: this.nbExtension.keySet()){
			sb.append(extension).append(": ").append(getPercentage(extension)).append("%\n\r");
		}
		return sb.toString();
	}
	
	private double getPercentage(String extension) {
		int occurences = this.nbExtension.get(extension);
		return (double) occurences / (double) this.totalImages * 100;
	}
	
}