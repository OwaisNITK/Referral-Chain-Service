package com.onmobile.vol.referralchain.app.dto;

public enum ReferralType {

	PARENT("parent"),
	CHILD("child");
	
	ReferralType(String referralType) {
		this.referralType = referralType;
	}

	private final String referralType;

	public String getReferralType() {
		return referralType;
	}	
}