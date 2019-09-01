package com.onmobile.vol.referralchain.app.config;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
		return new PoolingHttpClientConnectionManager();
	}
}