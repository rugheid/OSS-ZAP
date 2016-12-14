package org.zaproxy.zap.extension.pscan.contentreport;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.parosproxy.paros.network.HttpMessage;

public class SiteStatistic {
	
	ArrayList<Statistic> statistics = new ArrayList<>();
	
	SiteStatistic() {
		statistics.add(new ImagePropertyStatistic("height", BufferedImage::getHeight));
		statistics.add(new ImagePropertyStatistic("width", BufferedImage::getWidth));
		statistics.add(new ImageNumberStatistic("file size", msg -> msg.getResponseBody().getBytes().length));
		statistics.add(new ExtensionPercentageStatistic());
	}
	
	public void addEntry(HttpMessage msg) throws IOException {
		statistics.stream().forEach(stat -> stat.addEntry(msg));
	}
	
	public String toReportString() {
		List<String> reports = statistics.stream()
				.map(stat -> stat.toReportString())
				.collect(Collectors.toList());
		return StringUtils.join(reports, "\n------------------------------------\n");
	}
}
