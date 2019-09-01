package com.onmobile.vol.referralchain.app.dto;


import com.onmobile.vol.referralchain.app.dataaccess.domain.SubscriptionType;

public class PrismCallBackRequestDto {
	
	private String msisdn;
	private String mode;
	private PrismStatus status;
	private PrismAction action;
	private SubscriptionType type;
	private String externalId;
	private String operator;
	private ReferralType referralType;
	
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public SubscriptionType getType() {
		return type;
	}
	public void setType(SubscriptionType type) {
		this.type = type;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public ReferralType getReferralType() {
		return referralType;
	}
	public void setReferralType(ReferralType referralType) {
		this.referralType = referralType;
	}
	public PrismStatus getStatus() {
		return status;
	}
	public void setStatus(PrismStatus status) {
		this.status = status;
	}
	public PrismAction getAction() {
		return action;
	}
	public void setAction(PrismAction action) {
		this.action = action;
	}
	@Override
	public String toString() {
		return "PrismCallBackRequestDto [msisdn=" + msisdn + ", mode=" + mode + ", status=" + status + ", action="
				+ action + ", type=" + type + ", externalId=" + externalId + ", operator=" + operator
				+ ", referralType=" + referralType + "]";
	}
	
	
}
