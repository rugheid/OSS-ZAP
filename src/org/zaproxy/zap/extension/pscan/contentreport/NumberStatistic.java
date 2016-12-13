package org.zaproxy.zap.extension.pscan.contentreport;

import java.util.ArrayList;

public class NumberStatistic implements Statistic {
	
	
	private ArrayList<Integer> data = new ArrayList<Integer>();
	private int maximum = Integer.MIN_VALUE;
	private String maxURI;
	private int minimum = Integer.MAX_VALUE;
	private String minURI;
	
	private String name;
	
	NumberStatistic(String name) {
		this.name = name;
	}
	
	public void addEntry(int entry, String uri) {
		data.add(entry);
		if (entry > maximum) {
			maximum = entry;
			maxURI = uri;
		}
		if (entry < minimum) {
			minimum = entry;
			minURI = uri;
		}
	}

	public int getMaximum() {
		return maximum;
	}
	
	public String getMaximumURI() {
		return maxURI;
	}
	
	public int getMinimum() {
		return minimum;
	}
	
	public String getMinimumURI() {
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
	
	public String toReportString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Maximum ").append(this.name).append(": ");
		sb.append(this.getMaximum()).append("\n");
		sb.append("URI Maximum ").append(this.name).append(": ");
		sb.append(this.getMaximumURI()).append("\n");
		sb.append("Minimum ").append(this.name).append(": ");
		sb.append(this.getMinimum()).append("\n");
		sb.append("URI Minimum ").append(this.name).append(": ");
		sb.append(this.getMinimumURI()).append("\n");
		sb.append("Average ").append(this.name).append(": ");
		sb.append(this.getAverage()).append("\n");
		sb.append("Median ").append(this.name).append(": ");
		sb.append(this.getMedian()).append("\n");
		return sb.toString();
	}
}