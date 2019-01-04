package com.using.common.core.http.httpclient;

import java.io.IOException;
import java.io.InputStream;

public interface HttpResponseCallBack {

	public void processResponse(InputStream responseBody) throws IOException;
}
