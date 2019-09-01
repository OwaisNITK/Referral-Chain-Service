package com.onmobile.vol.referralchain.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onmobile.vol.referralchain.app.constants.FieldNameConstants;

public class StatusResponseDto {
	
	public enum ResponseStatus {
		FAILURE ,
		SUCCESS ;
	}
	
	@JsonProperty(FieldNameConstants.RESPONSE_STATUS)
	private ResponseStatus status ;
	
	public StatusResponseDto(ResponseStatus status) {
		super();
		this.status = status;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}	
}