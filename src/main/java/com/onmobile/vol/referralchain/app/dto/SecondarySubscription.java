package com.onmobile.vol.referralchain.app.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Status;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;

@Component
public class SecondarySubscription {
	
	private int childId ;
	private String cmsisdn ;
    private UserStatus userStatus;
    private Status status;
    private Date created;
    private Date lastUpdated;
    private Date expired;
	private String type ;
	private String mode ;
	private String externalId ;
	private String providerName ;
	private SMSStatus smsStatus ;
	private int smsRetryCountLeft ;
	private Date nextRetryTime ;
	private int parentSubscriptionId ;
	private PrimarySubscription parentSubscription;
	private String pmsisdn;
	private String pExternalId ;
	
	public SecondarySubscription() {}
	
	public int getChildId() {
		return childId;
	}

	public void setChildId(int childId) {
		this.childId = childId;
	}

	public String getCmsisdn() {
		return cmsisdn;
	}

	public void setCmsisdn(String cmsisdn) {
		this.cmsisdn = cmsisdn;
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Date getExpired() {
		return expired;
	}

	public void setExpired(Date expired) {
		this.expired = expired;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public SMSStatus getSmsStatus() {
		return smsStatus;
	}

	public void setSmsStatus(SMSStatus smsStatus) {
		this.smsStatus = smsStatus;
	}

	public int getSmsRetryCountLeft() {
		return smsRetryCountLeft;
	}

	public void setSmsRetryCountLeft(int smsRetryCountLeft) {
		this.smsRetryCountLeft = smsRetryCountLeft;
	}

	public Date getNextRetryTime() {
		return nextRetryTime;
	}

	public void setNextRetryTime(Date nextRetryTime) {
		this.nextRetryTime = nextRetryTime;
	}
	
	public int getParentSubscriptionId() {
		return parentSubscriptionId;
	}

	public void setParentSubscriptionId(int parentSubscriptionId) {
		this.parentSubscriptionId = parentSubscriptionId;
	}

	public PrimarySubscription getParentSubscription() {
		return parentSubscription;
	}

	public void setParentSubscription(PrimarySubscription parentSubscription) {
		this.parentSubscription = parentSubscription;
	}

	public String getPmsisdn() {
		return pmsisdn;
	}

	public void setPmsisdn(String pmsisdn) {
		this.pmsisdn = pmsisdn;
	}

	public String getpExternalId() {
		return pExternalId;
	}

	public void setpExternalId(String pExternalId) {
		this.pExternalId = pExternalId;
	}
}