package org.zaproxy.zap.extension.pscan.contentreport;

import org.parosproxy.paros.network.HttpMessage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageHeightStatistic extends ImageNumberStatistic {

	ImageHeightStatistic() {
		super("height");
	}

	@Override
	int parseMessage(HttpMessage msg) throws IOException {
		BufferedImage image = imageFromBytes(msg.getResponseBody().getBytes());
		return image.getHeight();
	}

}
