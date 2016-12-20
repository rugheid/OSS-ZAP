package org.zaproxy.zap.extension.pscan.contentreport;

import org.apache.commons.lang.StringUtils;
import org.parosproxy.paros.network.HttpMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SiteStatistic {

	private ArrayList<Statistic> statistics = new ArrayList<>();
	
	SiteStatistic() {
	    statistics.add(new ImageHeightStatistic());
	    statistics.add(new ImageWidthStatistic());
	    statistics.add(new ImageSizeStatistic());
	    statistics.add(new ImageExtensionStatistic());
	}

	public void notifyStatistics(HttpMessage msg) {
        statistics.forEach(stat -> stat.update(msg));
	}

	public String toReportString() {
        List<String> reports = statistics.stream()
                                         .map(Statistic::toReportString)
                                         .collect(Collectors.toList());
        return StringUtils.join(reports, "\n------------------------------------\n");
	}
}
