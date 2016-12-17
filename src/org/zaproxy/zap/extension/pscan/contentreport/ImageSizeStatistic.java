package org.zaproxy.zap.extension.pscan.contentreport;

import org.parosproxy.paros.network.HttpMessage;

import java.io.IOException;

public class ImageSizeStatistic extends ImageNumberStatistic {

	ImageSizeStatistic() {
	    super("file size");
    }

	@Override
	int parseMessage(HttpMessage msg) throws IOException {
		return msg.getResponseBody().getBytes().length;
	}

}
