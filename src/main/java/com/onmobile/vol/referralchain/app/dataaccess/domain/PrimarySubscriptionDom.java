package com.onmobile.vol.referralchain.app.dataaccess.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "primary_subscription")
public class PrimarySubscriptionDom {
	
	@Id
	@GeneratedValue
	@Column(name = "parent_id")
	private int parentId ;
	
	@Column(name = "pmsisdn")
	private String pmsisdn ;
	
	@Enumerated(EnumType.ORDINAL)
    @Column(name = "user_status")
    private UserStatus userStatus;
	
    @Column(name = "created", columnDefinition = "TIMESTAMP NULL")
    private Date created;
 
    @Column(name = "last_updated", insertable=false, updatable=false)
    private Date lastUpdated;
	
	@Column(name = "total_count")
	private int totalCount ;
	
	@Column(name = "count_left")
	private int countLeft ; 
	
	@Column(name = "external_id")
	private String externalId ;
	
	@Column(name = "operator")
	private String operator ;
	
	@Column(name = "circle")
	private String circle ;
	
	@Column(name = "type")
	private String type ;
	
	@Column(name = "mode")
	private String mode ;
	
	@OneToMany(mappedBy = "parentSubscriptionDom", fetch = FetchType.LAZY, orphanRemoval = true)
	private List<SecondarySubscriptionDom> childs = new ArrayList<SecondarySubscriptionDom>();

	public PrimarySubscriptionDom() {}

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

	public List<SecondarySubscriptionDom> getChilds() {
		return childs;
	}

	public void setChilds(List<SecondarySubscriptionDom> childs) {
		this.childs = childs;
	}
}