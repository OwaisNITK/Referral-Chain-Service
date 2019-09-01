package com.onmobile.vol.referralchain.app.dto;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;

@Component
public class PrimarySubscription {
	
	private int parentId ;
	private String pmsisdn ;
	private UserStatus userStatus;
	private Date created;
    private Date lastUpdated;
	private int totalCount ;
	private int countLeft ; 
	private String externalId ;
	private String operator ;
	private String circle ;
	private String type ;
	private String mode ;
	private List<SecondarySubscription> childs ;
	
	public PrimarySubscription() {}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getPmsisdn() {
		return pmsisdn;
	}

	public void setPmsisdn(String pmsisdn) {
		this.pmsisdn = pmsisdn;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
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

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getCountLeft() {
		return countLeft;
	}

	public void setCountLeft(int countLeft) {
		this.countLeft = countLeft;
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

	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
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

	public List<SecondarySubscription> getChilds() {
		return childs;
	}

	public void setChilds(List<SecondarySubscription> childs) {
		this.childs = childs;
	}
}