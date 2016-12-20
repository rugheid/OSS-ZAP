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

import net.htmlparser.jericho.Source;
import org.apache.commons.httpclient.URIException;
import org.apache.log4j.Logger;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.core.scanner.Category;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.pscan.PassiveScanThread;
import org.zaproxy.zap.extension.pscan.PluginPassiveScanner;

import java.util.*;

/**
 * Based on the example passive scan rule, found at 
 * http://zaproxy.blogspot.co.uk/2014/04/hacking-zap-3-passive-scan-rules.html
 * @author Groep 5
 */
public class ContentReportScanner extends PluginPassiveScanner {

	private static final Integer[] HISTORY_TYPES = new Integer[] {
			HistoryReference.TYPE_PROXIED, HistoryReference.TYPE_ZAP_USER,
			HistoryReference.TYPE_SPIDER, HistoryReference.TYPE_SPIDER_AJAX,
            HistoryReference.TYPE_HIDDEN};
	private static final Set<Integer> HISTORY_TYPES_SET =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(HISTORY_TYPES)));
	
	private PassiveScanThread parent = null;
	private static final Logger logger = Logger.getLogger(ContentReportScanner.class);
	
	private HashMap<String, SiteStatistic> statisticMap = new HashMap<>();
	private HashMap<String, Alert> alertMap = new HashMap<>();

	@Override
	public void setParent (PassiveScanThread parent) {
		this.parent = parent;
	}

	@Override
	public void scanHttpRequestSend(HttpMessage msg, int id) {}

	@Override
	public int getPluginId() {
		return 61337;
	}

	@Override
	public void scanHttpResponseReceive(HttpMessage msg, int id, Source source) {
		if (msg.getResponseHeader().isEmpty() || !msg.getResponseHeader().isImage()) return;

		try {
			SiteStatistic statistics = this.getStatisticsForSite(msg);
			Alert alert = this.getAlertForSite(msg, id);
			statistics.notifyStatistics(msg);
			alert.setEvidence(statistics.toReportString());
		} catch (URIException e) {
			logger.error("URIException in the ContentReportScanner", e);
		}
	}
	
	private SiteStatistic getStatisticsForSite(HttpMessage msg) throws URIException {
		String site = msg.getRequestHeader().getURI().getHost();
		if (!this.statisticMap.containsKey(site)) {
			this.statisticMap.put(site, new SiteStatistic());
		}
		return this.statisticMap.get(site);
	}
	
	private Alert getAlertForSite(HttpMessage msg, int id) throws URIException {
		String site = msg.getRequestHeader().getURI().getHost();
		if (!this.alertMap.containsKey(site)) {
			Alert alert = prepareAlert(msg);
			parent.raiseAlert(id, alert);
			this.alertMap.put(site, alert);
		}
		return this.alertMap.get(site);
	}

	private Alert prepareAlert(HttpMessage msg) throws URIException {
		Alert alert = new Alert(getPluginId(), Alert.RISK_INFO, Alert.CONFIDENCE_HIGH, 
		    	getName());
		alert.setDetail(
			getDescription(), 
			msg.getRequestHeader().getURI().getHost(),
			"",	// Param
			"", // Attack
			"", // Other info
			"", // Solution
		    "", // Reference
		    "no decodable images", // Evidence
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
    	return "Statistics about images on visited websites.";
    }

    public int getCategory() {
        return Category.INFO_GATHER;
    }

	@Override
	public boolean appliesToHistoryType(int historyType) {
		return HISTORY_TYPES_SET.contains(historyType);
	}

}
