package com.onmobile.vol.referralchain.app.config;

public interface ProviderDetailsResolver {

	public String resolveRbtUrlForProvider(String providerName);
	
	public String resolveUmpUrlForProvider(String providerName);
	
	public String resolveSenderNoForProvider(String providerName);
	
	public String resolveDefaultUMPMessageForProvider(String providerName);
}