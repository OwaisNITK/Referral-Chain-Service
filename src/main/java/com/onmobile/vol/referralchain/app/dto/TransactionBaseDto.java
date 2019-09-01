package com.onmobile.vol.referralchain.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onmobile.vol.referralchain.app.constants.FieldNameConstants;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Action;

public class TransactionBaseDto {

	
	@JsonProperty(FieldNameConstants.ACTION)
	private Action action;
	
	@JsonProperty(FieldNameConstants.DATE)
	private String date ; 
	
	@JsonProperty(FieldNameConstants.MESSAGE)
	private String message;
	
	@JsonProperty(FieldNameConstants.CHILD_MSISDN)
	private String childMsisdn;
	
	
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String localDateTime) {
		this.date = localDateTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getChildMsisdn() {
		return childMsisdn;
	}
	public void setChildMsisdn(String childMsisdn) {
		this.childMsisdn = childMsisdn;
	}
	
	@Override
	public String toString() {
		return "Transaction [action=" + action + ", date=" + date + ", message=" + message + ", childMsisdn="
				+ childMsisdn + "]";
	}

	
}
