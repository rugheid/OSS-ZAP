package org.zaproxy.zap.extension.pscan.contentreport;

import java.util.ArrayList;

import org.apache.commons.httpclient.URI;

public class Statistic {
	
	
	private ArrayList<Integer> data = new ArrayList<Integer>();
	private int maximum = Integer.MIN_VALUE;
	private URI maxURI;
	private int minimum = Integer.MAX_VALUE;
	private URI minURI;
	
	private String name;
	
	Statistic(String name) {
		this.name = name;
	}
	
	public void addEntry(int entry, URI uri) {
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
		return data.get(data.size()/2);
	}
	
	public int getAverage() {
		return data.stream().mapToInt(a -> a).sum() / data.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Maximum ").append(this.name).append(": ");
		sb.append(this.getMaximum()).append("\n");
		sb.append("Minimum ").append(this.name).append(": ");
		sb.append(this.getMinimum()).append("\n");
		sb.append("Average ").append(this.name).append(": ");
		sb.append(this.getAverage()).append("\n");
		sb.append("Median ").append(this.name).append(": ");
		sb.append(this.getMedian()).append("\n");
		return sb.toString();
	}
}