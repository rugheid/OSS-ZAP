package org.zaproxy.zap.extension.pscan.contentreport;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.parosproxy.paros.network.HttpMessage;

public class WidthStatistic extends ImageNumberStatistic {

	public static final String name = "width";

	@Override
	int parseMessage(HttpMessage msg) throws IOException {
		BufferedImage image = imageFromBytes(msg.getResponseBody().getBytes());
		return image.getWidth();
	}

}
