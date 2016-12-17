package org.zaproxy.zap.extension.pscan.contentreport;

import org.apache.commons.lang.StringUtils;
import org.parosproxy.paros.network.HttpMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SiteStatistic implements Statistic {

	private ArrayList<Statistic> statistics = new ArrayList<>();
	
	SiteStatistic() {
	    statistics.add(new ImageHeightStatistic());
	    statistics.add(new ImageWidthStatistic());
	    statistics.add(new ImageSizeStatistic());
	    statistics.add(new ImageExtensionStatistic());
	}

	@Override
	public void addEntry(HttpMessage msg) {
        statistics.forEach(stat -> stat.addEntry(msg));
	}

	@Override
	public String toReportString() {
        List<String> reports = statistics.stream()
                                         .map(Statistic::toReportString)
                                         .collect(Collectors.toList());
        return StringUtils.join(reports, "\n------------------------------------\n");
	}
}
