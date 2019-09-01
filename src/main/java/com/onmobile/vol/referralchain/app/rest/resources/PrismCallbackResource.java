package com.onmobile.vol.referralchain.app.rest.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onmobile.vol.referralchain.app.dataaccess.domain.SubscriptionType;
import com.onmobile.vol.referralchain.app.dto.PrismAction;
import com.onmobile.vol.referralchain.app.dto.PrismCallBackRequestDto;
import com.onmobile.vol.referralchain.app.dto.PrismStatus;
import com.onmobile.vol.referralchain.app.dto.ReferralType;
import com.onmobile.vol.referralchain.app.dto.StatusResponseDto;
import com.onmobile.vol.referralchain.app.dto.StatusResponseDto.ResponseStatus;
import com.onmobile.vol.referralchain.app.errors.ErrorCode;
import com.onmobile.vol.referralchain.app.exceptions.ReferralChainServiceException;
import com.onmobile.vol.referralchain.app.rest.service.PrismCallbackService;

@RestController
@RequestMapping("/prism/callback")
public class PrismCallbackResource {

	private static String RBT_ACT = "RBT_ACT_";
	
	private static Logger logger = LogManager.getLogger(PrismCallbackResource.class);
	
	@Autowired
	PrismCallbackService prismCallbackService;
	
	@PutMapping("/{msisdn}")
	public StatusResponseDto registerPrimarySubscription(
			@PathVariable(value="msisdn") String msisdn ,
			@RequestParam(value="mode", required= true) String mode,
			@RequestParam(value="status", required= true) PrismStatus status,
			@RequestParam(value="action", required= true) PrismAction action,
			@RequestParam(value="type", required=false, defaultValue="RINGBACK") SubscriptionType type,
			@RequestParam(value="external_id", required= true) String externalId,
			@RequestParam(value="operator", required=false) String operator,
			@RequestParam(value="referral_type", required=true) String referralType) {
		
		ReferralType refflTyp = null;
		externalId = (externalId != null  && externalId.indexOf(RBT_ACT) != -1 ? externalId.substring(RBT_ACT.length()) : externalId );
		try{
			refflTyp = ReferralType.valueOf(referralType.toUpperCase());	
		}
		catch(IllegalArgumentException ex) {
			throw new ReferralChainServiceException(ErrorCode.MISSING_PARAMETER, ErrorCode.MISSING_PARAMETER, "referral Type is invalid");
		}
		
		logger.debug("PrismCallBackAPI starts for msisdn : "+msisdn);
		PrismCallBackRequestDto prismCallBackRequestDto = new PrismCallBackRequestDto();
		prismCallBackRequestDto.setAction(action);
		prismCallBackRequestDto.setExternalId(externalId);
		prismCallBackRequestDto.setMode(mode);
		prismCallBackRequestDto.setMsisdn(msisdn);
		prismCallBackRequestDto.setOperator(operator);
		prismCallBackRequestDto.setReferralType(refflTyp);
		prismCallBackRequestDto.setStatus(status);
		
		ResponseStatus statusResponseDto=  prismCallbackService.prismCallBack(prismCallBackRequestDto);
		logger.debug("prismcallback  response  : {}  for  msisdn  is  : {} ",statusResponseDto.name(),msisdn);
		
		return new StatusResponseDto(statusResponseDto);
	}
}
