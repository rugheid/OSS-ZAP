package org.zaproxy.zap.extension.pscan.contentreport;

import org.parosproxy.paros.network.HttpMessage;

public interface Statistic {
	
	void addEntry(HttpMessage msg);
	
	String toReportString();

}
