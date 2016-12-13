package org.zaproxy.zap.extension.pscan.contentreport;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.parosproxy.paros.network.HttpMessage;

public class HeightStatistic extends ImageNumberStatistic {

	static String name = "height";

	@Override
	int parseMessage(HttpMessage msg) throws IOException {
		BufferedImage image = imageFromBytes(msg.getResponseBody().getBytes());
		return image.getHeight();
	}

}
