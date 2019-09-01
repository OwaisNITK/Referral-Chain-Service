package com.onmobile.vol.referralchain.app.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "referralchain.external-id")
public class ExternalIdMappingConfig {

	private Map<String,String> mapping ;

	public Map<String, String> getMapping() {
		return mapping;
	}

	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}
	
	public String getChildExternalId(String parentExternalId) {
		return this.mapping.get(parentExternalId).split(":")[0];
	}
	
	public String getSongPriceId(String externalId) {
		return this.mapping.get(externalId).split(":")[1];
	}
	
	public String getTotalCount(String parentExternal_Id) {
		return this.mapping.get(parentExternal_Id).split(":")[2];
	}
}