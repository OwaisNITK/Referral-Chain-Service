package com.onmobile.vol.referralchain.app.client;

import org.apache.http.impl.client.CloseableHttpClient;

public interface HttpClientFactory {

	CloseableHttpClient createHttpClient();
}
