package org.zaproxy.zap.extension.pscan.contentreport;

import java.io.IOException;

import org.parosproxy.paros.network.HttpMessage;

public class SiteStatistic {
	
	public ImageNumberStatistic imageHeight;
	public ImageNumberStatistic imageWidth;
	public ImageNumberStatistic imageFileSize;
	public ExtensionPercentageStatistic imageType;
	
	SiteStatistic() {
		this.imageHeight = new HeightStatistic();
		this.imageWidth = new WidthStatistic();
		this.imageFileSize = new ImageSizeStatistic();
		this.imageType = new ExtensionPercentageStatistic();
	}
	
	public void addEntry(HttpMessage msg) throws IOException {
		this.imageHeight.addEntry(msg);
		this.imageWidth.addEntry(msg);
		this.imageFileSize.addEntry(msg);
		this.imageType.addEntry(msg);
	}



	
	public String toReportString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.imageHeight.toReportString()).append("\n------------------------------------\n");
		sb.append(this.imageWidth.toReportString()).append("\n------------------------------------\n");
		sb.append(this.imageFileSize.toReportString()).append("\n------------------------------------\n");
		sb.append(this.imageType.toReportString());
		return sb.toString();
	}
}
