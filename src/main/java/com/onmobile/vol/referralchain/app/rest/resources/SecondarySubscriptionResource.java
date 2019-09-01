package com.onmobile.vol.referralchain.app.rest.resources;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onmobile.vol.referralchain.app.config.ExternalIdMappingConfig;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Action;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Status;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;
import com.onmobile.vol.referralchain.app.dto.SecondarySubscription;
import com.onmobile.vol.referralchain.app.dto.SecondarySubscriptionStatusResponseDto;
import com.onmobile.vol.referralchain.app.dto.StatusResponseDto;
import com.onmobile.vol.referralchain.app.errors.ErrorCode;
import com.onmobile.vol.referralchain.app.exceptions.ReferralChainServiceException;
import com.onmobile.vol.referralchain.app.rest.service.SecondarySubscriptionService;

@RestController
@RequestMapping("/child")
public class SecondarySubscriptionResource {
	
	public static Logger logger  = LogManager.getLogger(SecondarySubscriptionResource.class);
	
	@Autowired
	SecondarySubscriptionService secondarySubscriptionService;
	
	@Autowired
	ExternalIdMappingConfig externalIdMap;
	
	@GetMapping("/{cmsisdn}")
	public SecondarySubscriptionStatusResponseDto getSecondarySubscriptionStatus(
			@PathVariable(value="cmsisdn") String cmsisdn ,
			@RequestParam(value="mode", required= true) String mode) {
		
		logger.debug("getSecondarySubscriptionStatus API starts for Child Msisdn : {} ",cmsisdn);
		SecondarySubscription ss = secondarySubscriptionService.getSecondarySubscriptionByMsisdn(cmsisdn) ;
		logger.debug("Child Subscription Found for child msisdn : {}  so preparing DTO",cmsisdn);
		if(ss==null) {
			logger.error("No Child Subscription Found for child msisdn : {} ",cmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_NOT_FOUND,
					"No Child Subscription Found");
		}
				
		// Preprare Child Subscription Dto
		SecondarySubscriptionStatusResponseDto sssResponseDto = new SecondarySubscriptionStatusResponseDto();
		sssResponseDto.setMsisdn(ss.getCmsisdn());
		sssResponseDto.setUserStatus(ss.getUserStatus());
		sssResponseDto.setStatus(ss.getStatus());
		sssResponseDto.setExpired(ss.getExpired());
		sssResponseDto.setPmsisdn(ss.getPmsisdn());
		
		if(ss.getUserStatus()==UserStatus.PENDING) {
			// Resolve ChildExternalId And SongPriceId From Configuration 
			String resolvedExternalId = externalIdMap.getChildExternalId(ss.getpExternalId());
			String resolvedSongPriceId = externalIdMap.getSongPriceId(ss.getpExternalId());
			sssResponseDto.setExternalId(resolvedExternalId);
			sssResponseDto.setSong_price_id(resolvedSongPriceId);
			logger.debug("for child msisdn : {} , userStatus is :{} so configured ExternalId  is : {} and SongPriceId is  : {} ",cmsisdn,UserStatus.PENDING,resolvedExternalId,resolvedSongPriceId);
		} else {
			sssResponseDto.setExternalId(ss.getExternalId());
			String resolvedSongPriceId = externalIdMap.getSongPriceId(ss.getExternalId());
			sssResponseDto.setSong_price_id(resolvedSongPriceId);
		}
		logger.debug("Child Subscription : {}  for child msisdn : {} " ,sssResponseDto.toString(),cmsisdn);
		return sssResponseDto ;
	}
	
	@DeleteMapping("/{cmsisdn}")
	public StatusResponseDto rejectSecondarySubscription(
			@PathVariable(value="cmsisdn") String cmsisdn ,
			@RequestParam(value="mode", required= true) String mode) {
		logger.debug("rejectSecondarySubscription API starts for Child Msisdn : {} ",cmsisdn);
		// Check If cmsisdn already exist as an active or pending child
		SecondarySubscription existingSsub = secondarySubscriptionService.getSecondarySubscriptionByMsisdn(cmsisdn);
		
		if(existingSsub== null) {
			logger.error("Child is already removed for msisdn  : {} ",cmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_NOT_FOUND,
					"Child is already removed");
		}
				
		if(existingSsub.getUserStatus() == UserStatus.PENDING) {
				logger.debug("child msisdn : {} status is Pending so deleting the child Subscription and updating the count left for parent",cmsisdn);
				secondarySubscriptionService.deleteSecondarySubscriptionAndUpdateCountLeft(existingSsub,Action.CHILD_REJECT);
				logger.debug("child Subscription is deleted for msisdn : {} ",cmsisdn);
		} else {
			logger.error("For child msisdn : {}  the status is  : {} that is invalid so throwing Child status Invalid Exception",cmsisdn,existingSsub.getUserStatus());
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_STATUS_INVALID,
					"Child User Status is invalid");
		}
		
		return new StatusResponseDto(StatusResponseDto.ResponseStatus.SUCCESS);
	}
	
	@PutMapping("/{cmsisdn}/accept")
	public StatusResponseDto acceptSecondarySubscription(
			@PathVariable(value="cmsisdn") String cmsisdn ,
			@RequestParam(value="mode", required= true) String mode) {
		
		logger.debug("AcceptSecondarySubscription API starts for Child Msisdn : {} ",cmsisdn);
		// Check If cmsisdn already exist as pending child
		SecondarySubscription existingSsub = secondarySubscriptionService.getSecondarySubscriptionByMsisdn(cmsisdn);
		
		if(existingSsub== null) {
			logger.error("Child is already removed for msisdn  : {} ",cmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_NOT_FOUND,
					"Child is already removed");
		}
		
		if(existingSsub.getStatus() == Status.ACCEPTED) {
			return new StatusResponseDto(StatusResponseDto.ResponseStatus.SUCCESS);
		}
		
		if((existingSsub.getUserStatus() == UserStatus.PENDING)
			&& (existingSsub.getStatus() == Status.PENDING)
			&& (existingSsub.getExpired().after(new Date()))) {
				logger.debug("Updating child Gift Status : {} to ACCEPT for AcceptGift API",existingSsub.getStatus());
				secondarySubscriptionService.updateSecondarySubscriptionForGiftAccept(existingSsub);
				logger.debug("child Gift Status is updated for msisdn : {} ",cmsisdn);
		} else {
			logger.error("Child status : {} is invalid to accept the offer for child msisdn : {}",existingSsub.getStatus(),cmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.GIFT_EXPIRED,
					"Gift is expired");
		}
		
		return new StatusResponseDto(StatusResponseDto.ResponseStatus.SUCCESS);
	}

}