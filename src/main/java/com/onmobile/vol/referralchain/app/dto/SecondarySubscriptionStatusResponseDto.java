package com.onmobile.vol.referralchain.app.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onmobile.vol.referralchain.app.constants.FieldNameConstants;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Status;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;

public class SecondarySubscriptionStatusResponseDto {
	
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
	
	@JsonProperty(FieldNameConstants.SONG_PRICE_ID)
	private String song_price_id;
	
	@JsonProperty(FieldNameConstants.PARENT_ID)
	private String pmsisdn;
	
	public SecondarySubscriptionStatusResponseDto(){}

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

	public String getSong_price_id() {
		return song_price_id;
	}

	public void setSong_price_id(String song_price_id) {
		this.song_price_id = song_price_id;
	}

	public String getPmsisdn() {
		return pmsisdn;
	}

	public void setPmsisdn(String pmsisdn) {
		this.pmsisdn = pmsisdn;
	}

	@Override
	public String toString() {
		return "SecondarySubscriptionStatusResponseDto [msisdn=" + msisdn + ", userStatus=" + userStatus + ", status="
				+ status + ", expired=" + expired + ", externalId=" + externalId + ", song_price_id=" + song_price_id
				+ ", pmsisdn=" + pmsisdn + "]";
	}
}