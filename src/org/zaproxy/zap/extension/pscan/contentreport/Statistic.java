package org.zaproxy.zap.extension.pscan.contentreport;

public interface Statistic {
	
	void addEntry(int entry, String label);
	
	String toReportString();

}
