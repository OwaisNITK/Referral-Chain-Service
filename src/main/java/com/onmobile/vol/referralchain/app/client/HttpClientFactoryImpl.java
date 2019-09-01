package com.onmobile.vol.referralchain.app.client;

import javax.annotation.PostConstruct;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpClientFactoryImpl implements HttpClientFactory {

	@Value("${referralchain.httpclient.connectTimeoutMillis:10000}")
	private Integer connectTimeout;

	@Value("${referralchain.httpclient.connectionRequestTimeoutMillis:10000}")
	private Integer connectionRequestTimeout;

	@Value("${referralchain.httpclient.socketTimeoutMillis:10000}")
	private Integer socketTimeout;

	private RequestConfig requestConfig;

	private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

	public HttpClientFactoryImpl(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
		super();
		this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
	}

	@PostConstruct
	void postConstruct() {
		requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
				.setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();
	}

	@Override
	public CloseableHttpClient createHttpClient() {
		// TODO: ADD SNMP TRAP
		CloseableHttpClient closeableHttpClient = HttpClients.custom()
				.setConnectionManager(poolingHttpClientConnectionManager)
				.setDefaultRequestConfig(requestConfig)
				.build();
		return closeableHttpClient;
	}

}