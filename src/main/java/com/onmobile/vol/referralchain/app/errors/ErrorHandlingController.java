package com.onmobile.vol.referralchain.app.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.onmobile.vol.referralchain.app.exceptions.ReferralChainServiceException;

@ControllerAdvice
public class ErrorHandlingController {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> generalExceptionHandler(Exception e) throws Exception {
		
		ErrorDto error = new ErrorDto() ;
		
		error.setCode(ErrorCode.REFERRAL_SERVICE_ERROR);
		error.setSubCode(ErrorCode.INTERNAL_ERROR);
		error.setDescription("Internal Server Error. You can try after some time");
		return new ResponseEntity<ErrorDto>(error, HttpStatus.valueOf(error.getCode().getResponseStatus()));
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorDto> MissingParameterHandler(MissingServletRequestParameterException ex) throws Exception {
		
		String missingParamName = ex.getParameterName();
		
		ErrorDto error = new ErrorDto() ;
		
		error.setCode(ErrorCode.REFERRAL_SERVICE_ERROR);
		error.setSubCode(ErrorCode.MISSING_PARAMETER);
		error.setDescription("Missing Parameter : " + missingParamName);
		return new ResponseEntity<ErrorDto>(error, HttpStatus.valueOf(error.getCode().getResponseStatus()));
	}
	
	@ExceptionHandler(ReferralChainServiceException.class)
	public ResponseEntity<ErrorDto> ReferralChainServiceExceptionHandler(ReferralChainServiceException ex) throws Exception {
		ErrorDto error = ex.getErrorDto();
		return new ResponseEntity<ErrorDto>(error, HttpStatus.valueOf(error.getCode().getResponseStatus()));	
	}	
}