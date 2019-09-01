package com.onmobile.vol.referralchain.app.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "referralchain.provider-details")
public class ProviderDetailsMappingConfig {
		
	private Map<String,ProviderDetails> provider = new HashMap<String,ProviderDetails>();

	public Map<String, ProviderDetails> getProvider() {
		return provider;
	}

	public void setProvider(Map<String, ProviderDetails> provider) {
		this.provider = provider;
	}
}