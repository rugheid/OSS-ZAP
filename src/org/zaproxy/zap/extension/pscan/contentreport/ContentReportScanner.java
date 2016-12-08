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
import java.util.HashMap;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.log4j.Logger;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.core.scanner.Category;
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
	
	/*
	 * file statistics: %png, jpg, ..
	 * image height
	 * image width
	 * image filesize
	 */
	
	// ExtensionAlert extAlert = (ExtensionAlert) Control.getSingleton().getExtensionLoader().getExtension(ExtensionAlert.NAME);


	@Override
	public void setParent (PassiveScanThread parent) {
		this.parent = parent;
	}

	@Override
	public void scanHttpRequestSend(HttpMessage msg, int id) {
		// You can also detect potential vulnerabilities here, with the same caveats as below.
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
		logger.info("\tpassed the contentreportscanner.. and image is " + msg.getResponseHeader().isImage() + " with url " + msg.getRequestHeader().getURI());
		if (msg.getResponseHeader().isEmpty() || !msg.getResponseHeader().isImage()) return;
		long start = System.currentTimeMillis();
		
		try {
			URI uri = msg.getRequestHeader().getURI();
			SiteStatistic statistics = this.getStatisticsForSite(uri);
			statistics.addEntry(msg);
		

		    Alert alert = new Alert(getPluginId(), Alert.RISK_INFO, Alert.CONFIDENCE_HIGH, 
			    	getName());
			    	alert.setDetail(
						getDescription(), 
						msg.getRequestHeader().getURI().toString(),
						"",	// Param
						"", // Attack
						"hi there", // Other info
						getSolution(), 
					    getReference(), 
					    statistics.toString(),	// Evidence
					    0,	// CWE Id
					    0,	// WASC Id
					    msg);
	
	    	parent.raiseAlert(id, alert);
			logger.debug("\tScan of record " + id + " took " + (System.currentTimeMillis() - start) + " ms");
			if (logger.isDebugEnabled()) {
				logger.debug("\tScan of record " + id + " took " + (System.currentTimeMillis() - start) + " ms");
			}
		} catch (URIException e) {
			// TODO
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	private SiteStatistic getStatisticsForSite(URI uri) throws URIException {
		String site = uri.getHost();
		if (!this.statisticMap.containsKey(site)) {
			this.statisticMap.put(site, new SiteStatistic());
		}
		SiteStatistic statistics = this.statisticMap.get(site);
		return statistics;
	}

	@Override
	public String getName() {
    	return "Content reporter scanner";
	}
	
    public String getDescription() {
    	return "See iteration 4"; //TODO
    }

    public int getCategory() {
        return Category.INFO_GATHER;
    }

    public String getSolution() {
    	return "You're stuck here forever"; //TODO
    }

    public String getReference() {
    	return "How can you stop him, when he's already here."; //TODO
    }

    @Override
	public boolean appliesToHistoryType (int historyType) {
		// TODO: base on HistoryReference (6: HIDDEN)
		return true;
	};
}
