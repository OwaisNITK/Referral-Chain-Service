package com.onmobile.vol.referralchain.app.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onmobile.vol.referralchain.app.constants.FieldNameConstants;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;

public class PrimarySubscriptionResponseDto {

	@JsonProperty(FieldNameConstants.MSISDN)
	private String msisdn ;

	@JsonProperty(FieldNameConstants.USER_STATUS)
	private UserStatus userStatus;
	
	@JsonProperty(FieldNameConstants.OPERATOR_NAME)
	private String operator;
	
	@JsonProperty(FieldNameConstants.CIRCLE_NAME)
	private String circle;
	
	@JsonProperty(FieldNameConstants.TOTAL_COUNT)
	private int totalCount;
	
	@JsonProperty(FieldNameConstants.COUNT_LEFT)
	private int countLeft;
	
	@JsonProperty(FieldNameConstants.EXTERNAL_ID)
	private String externalId;
	
	@JsonProperty(FieldNameConstants.CHILDS)
	private List<SecondarySubscriptionResponseDto> childs ;
	
	public PrimarySubscriptionResponseDto() {}

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

	public List<SecondarySubscriptionResponseDto> getChilds() {
		return childs;
	}

	public void setChilds(List<SecondarySubscriptionResponseDto> childs) {
		this.childs = childs;
	}
}