package com.onmobile.vol.referralchain.app.client.rbt;

public interface RbtClientService {
	
	public RbtSubscriptionResponseDto getRbtSubscriptionInfo(String msisdn, String providerName);
	
	public String DeactivateRbtSubscription(String msisdn, String providerName, String mode);
	
}