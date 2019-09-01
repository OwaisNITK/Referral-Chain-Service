package com.onmobile.vol.referralchain.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderDetailsResolverImpl implements ProviderDetailsResolver {
	
	@Autowired
	ProviderDetailsMappingConfig providerDetailsMappingConfig;
	
	@Override
	public String resolveRbtUrlForProvider(String providerName) {
		return providerDetailsMappingConfig.getProvider().get(providerName).getRbtUrl();
	}

	@Override
	public String resolveUmpUrlForProvider(String providerName) {
		return providerDetailsMappingConfig.getProvider().get(providerName).getUmpUrl();
	}

	@Override
	public String resolveSenderNoForProvider(String providerName) {
		return providerDetailsMappingConfig.getProvider().get(providerName).getSenderNo();
	}

	@Override
	public String resolveDefaultUMPMessageForProvider(String providerName) {
		return providerDetailsMappingConfig.getProvider().get(providerName).getDefaultMessage();
	}
}