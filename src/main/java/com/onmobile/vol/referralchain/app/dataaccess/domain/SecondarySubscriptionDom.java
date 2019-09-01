package com.onmobile.vol.referralchain.app.dataaccess.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "secondary_subscription")
public class SecondarySubscriptionDom {
	
	@Id @GeneratedValue
	@Column(name = "child_id")
	private int childId ;
	
	@Column(name = "cmsisdn")
	private String cmsisdn ;
	
	@Enumerated(EnumType.ORDINAL)
    @Column(name = "user_status")
    private UserStatus userStatus;
	
	@Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private Status status;
	
    @Column(name = "created", columnDefinition = "TIMESTAMP NULL")
    private Date created;
    
    @Column(name = "last_updated", insertable=false, updatable=false)
    private Date lastUpdated;
    
    @Column(name = "expired", columnDefinition = "TIMESTAMP NULL")
    private Date expired;
    
	@Column(name = "type")
	private String type ;
	
	@Column(name = "mode")
	private String mode ;
    
	@Column(name = "external_id")
	private String externalId ;
	
	@Column(name = "provider_name")
	private String providerName ;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "sms_status")
	private SMSStatus smsStatus ;
	
	@Column(name = "sms_retry_count_left")
	private int smsRetryCountLeft ;
	
	@Column(name = "next_retry_time")
	private Date nextRetryTime ;
	
	@JoinColumn(name="parent_id", referencedColumnName="parent_id")
	@ManyToOne(optional=false)
	private PrimarySubscriptionDom parentSubscriptionDom;
	
	public SecondarySubscriptionDom() {}

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

	public PrimarySubscriptionDom getParentSubscriptionDom() {
		return parentSubscriptionDom;
	}

	public void setParentSubscriptionDom(PrimarySubscriptionDom parentSubscription) {
		this.parentSubscriptionDom = parentSubscription;
	}
}