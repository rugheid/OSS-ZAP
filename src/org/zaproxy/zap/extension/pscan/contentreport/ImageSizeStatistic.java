package org.zaproxy.zap.extension.pscan.contentreport;

import java.io.IOException;

import org.parosproxy.paros.network.HttpMessage;

public class ImageSizeStatistic extends ImageNumberStatistic {

	static final String name = "file size";

	@Override
	int parseMessage(HttpMessage msg) throws IOException {
		return msg.getResponseBody().getBytes().length;
	}

}
