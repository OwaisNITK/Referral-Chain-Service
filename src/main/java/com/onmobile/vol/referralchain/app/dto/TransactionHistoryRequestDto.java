package com.onmobile.vol.referralchain.app.dto;

import java.util.Date;

public class TransactionHistoryRequestDto {
	
	private String msisdn ;
	private ReferralType type ;
	private String mode ;
	private Date startDate ;
	private Date endDate ;
	private int offset ;
	private int max ;
	
	public TransactionHistoryRequestDto() {}
	
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	public ReferralType getType() {
		return type;
	}

	public void setType(ReferralType type) {
		this.type = type;
	}

	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	} 	
}