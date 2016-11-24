package org.parosproxy.paros.extension.filter;

import org.parosproxy.paros.network.HttpMessage;

public class FilterInappropriateContent extends FilterAdaptor {


    @Override
    public int getId() {
        return 307;
    }

    @Override
    public String getName() {
        // TODO: Return name here
        return "DUMMY";
    }

    @Override
    public void onHttpRequestSend(HttpMessage httpMessage) {}

    @Override
    public void onHttpResponseReceive(HttpMessage httpMessage) {
        // TODO: Filter content here
    }
}
