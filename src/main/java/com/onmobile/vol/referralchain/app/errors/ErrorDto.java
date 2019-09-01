package com.onmobile.vol.referralchain.app.errors;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onmobile.vol.referralchain.app.constants.FieldNameConstants;

public class ErrorDto implements Serializable {
	 
    private static final long serialVersionUID = 797865342147900840L;
 
	@JsonProperty(FieldNameConstants.CODE)
    private ErrorCode code;
	
	@JsonProperty(FieldNameConstants.SUB_CODE)
    private ErrorCode subCode;
	
	@JsonProperty(FieldNameConstants.DESCRIPTION)
    private String description;
	
	public ErrorDto() {}
    
	public ErrorDto(ErrorCode code, ErrorCode subCode, String description) {
		super();
		this.code = code;
		this.subCode = subCode;
		this.description = description;
	}

	public ErrorCode getCode() {
		return code;
	}

	public void setCode(ErrorCode code) {
		this.code = code;
	}

	public ErrorCode getSubCode() {
		return subCode;
	}

	public void setSubCode(ErrorCode subCode) {
		this.subCode = subCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}