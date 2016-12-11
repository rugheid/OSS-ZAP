/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.zaproxy.zap.extension.pscan.contentreport;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.httpclient.URIException;
import org.apache.log4j.Logger;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.core.scanner.Category;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.pscan.PassiveScanThread;
import org.zaproxy.zap.extension.pscan.PluginPassiveScanner;

import net.htmlparser.jericho.Source;

/**
 * Based on the example passive scan rule, found at 
 * http://zaproxy.blogspot.co.uk/2014/04/hacking-zap-3-passive-scan-rules.html
 * @author Groep 5
 */
public class ContentReportScanner extends PluginPassiveScanner {

	private PassiveScanThread parent = null;
	private static final Logger logger = Logger.getLogger(ContentReportScanner.class);
	
	private HashMap<String, SiteStatistic> statisticMap = new HashMap<String, SiteStatistic>();

	@Override
	public void setParent (PassiveScanThread parent) {
		this.parent = parent;
	}

	@Override
	public void scanHttpRequestSend(HttpMessage msg, int id) {
	}

	@Override
	public int getPluginId() {
		/*
		 * This should be unique across all active and passive rules.
		 * The master list is https://github.com/zaproxy/zaproxy/blob/develop/src/doc/alerts.xml
		 */
		return 61337;
	}

	@Override
	public void scanHttpResponseReceive(HttpMessage msg, int id, Source source) {
		if (msg.getResponseHeader().isEmpty() || !msg.getResponseHeader().isImage()) return;
		long start = System.currentTimeMillis();
		
		try {
			
			SiteStatistic statistics = this.getStatisticsForSite(msg, id);
			statistics.addEntry(msg);
			
		} catch (URIException e) {
			// TODO
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("\tScan of record " + id + " took " + (System.currentTimeMillis() - start) + " ms");
	}
	
	private SiteStatistic getStatisticsForSite(HttpMessage msg, int id) throws URIException {
		String site = msg.getRequestHeader().getURI().getHost();
		if (!this.statisticMap.containsKey(site)) {
			Alert alert = prepareAlert(msg);
			parent.raiseAlert(id, alert);
			this.statisticMap.put(site, new SiteStatistic(alert));
		}
		SiteStatistic statistics = this.statisticMap.get(site);
		return statistics;
	}

	public Alert prepareAlert(HttpMessage msg) throws URIException {
		Alert alert = new Alert(getPluginId(), Alert.RISK_INFO, Alert.CONFIDENCE_HIGH, 
		    	getName());
		alert.setDetail(
			getDescription(), 
			msg.getRequestHeader().getURI().getHost().toString(),
			"",	// Param
			"", // Attack
			"", // Other info
			getSolution(), 
		    getReference(), 
		    "no images", // Evidence
		    0,	// CWE Id
		    0,	// WASC Id
		    msg); // HttpMessage
		return alert;
	}
	
	@Override
	public String getName() {
    	return "Image Statistics";
	}
	
    public String getDescription() {
    	return "Statistics about images on visited websites."; //TODO
    }

    public int getCategory() {
        return Category.INFO_GATHER;
    }

    public String getSolution() {
    	return ""; //TODO
    }

    public String getReference() {
    	return ""; //TODO
    }

	@Override
	public boolean appliesToHistoryType(int historyType) {
		Integer[] historyTypes = new Integer[] { HistoryReference.TYPE_PROXIED, HistoryReference.TYPE_ZAP_USER,
				HistoryReference.TYPE_SPIDER, HistoryReference.TYPE_SPIDER_AJAX, HistoryReference.TYPE_HIDDEN };
		Set<Integer> historyTypeSet = Collections.unmodifiableSet(new HashSet<Integer>(Arrays.asList(historyTypes)));
		return historyTypeSet.contains(historyType);
	};

}
