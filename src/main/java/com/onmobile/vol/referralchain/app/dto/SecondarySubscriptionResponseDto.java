package com.onmobile.vol.referralchain.app.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onmobile.vol.referralchain.app.constants.FieldNameConstants;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Status;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;

public class SecondarySubscriptionResponseDto {

	@JsonProperty(FieldNameConstants.MSISDN)
	private String msisdn ;

	@JsonProperty(FieldNameConstants.USER_STATUS)
	private UserStatus userStatus;
	
	@JsonProperty(FieldNameConstants.STATUS)
	private Status status;
	
	@JsonProperty(FieldNameConstants.EXPIRED)
	private Date expired;
	
	@JsonProperty(FieldNameConstants.EXTERNAL_ID)
	private String externalId;

	public SecondarySubscriptionResponseDto() {}
	
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getExpired() {
		return expired;
	}

	public void setExpired(Date expired) {
		this.expired = expired;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
}