package com.onmobile.vol.referralchain.app.config;

import org.springframework.stereotype.Component;

@Component
public class ProviderDetails {

	private String rbtUrl ;
	private String umpUrl ;
	private String senderNo ;
	private String defaultMessage ;

	public ProviderDetails() {}
	
	public String getRbtUrl() {
		return rbtUrl;
	}
	
	public void setRbtUrl(String rbtUrl) {
		this.rbtUrl = rbtUrl;
	}
	
	public String getUmpUrl() {
		return umpUrl;
	}
	
	public void setUmpUrl(String umpUrl) {
		this.umpUrl = umpUrl;
	}
	
	public String getSenderNo() {
		return senderNo;
	}
	
	public void setSenderNo(String senderNo) {
		this.senderNo = senderNo;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}
}