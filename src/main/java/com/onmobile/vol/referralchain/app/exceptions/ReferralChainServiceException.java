package com.onmobile.vol.referralchain.app.exceptions;

import com.onmobile.vol.referralchain.app.errors.ErrorCode;
import com.onmobile.vol.referralchain.app.errors.ErrorDto;

public class ReferralChainServiceException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private ErrorDto errorDto ;
	
	public ReferralChainServiceException(ErrorCode code, ErrorCode subCode, String message) {
		super(message);
		this.errorDto = new ErrorDto(code,subCode,message);
	}

	public ErrorDto getErrorDto() {
		return errorDto;
	}

	public void setErrorDto(ErrorDto errorDto) {
		this.errorDto = errorDto;
	}

	@Override
	public String toString() {
		return "ReferralChainServiceException [errorDto=" + errorDto + "]";
	}
}