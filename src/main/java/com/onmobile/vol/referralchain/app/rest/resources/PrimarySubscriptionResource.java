package com.onmobile.vol.referralchain.app.rest.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onmobile.vol.referralchain.app.client.rbt.RbtClientService;
import com.onmobile.vol.referralchain.app.client.rbt.RbtSubscriptionResponseDto;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Action;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Status;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SubscriptionType;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;
import com.onmobile.vol.referralchain.app.dto.PrimarySubscription;
import com.onmobile.vol.referralchain.app.dto.PrimarySubscriptionResponseDto;
import com.onmobile.vol.referralchain.app.dto.SecondarySubscription;
import com.onmobile.vol.referralchain.app.dto.SecondarySubscriptionResponseDto;
import com.onmobile.vol.referralchain.app.dto.StatusResponseDto;
import com.onmobile.vol.referralchain.app.errors.ErrorCode;
import com.onmobile.vol.referralchain.app.exceptions.ReferralChainServiceException;
import com.onmobile.vol.referralchain.app.rest.service.PrimarySubscriptionService;
import com.onmobile.vol.referralchain.app.rest.service.SecondarySubscriptionService;

@RestController
@RequestMapping("/parent")
public class PrimarySubscriptionResource {
	
	public static Logger logger  = LogManager.getLogger(PrimarySubscriptionResource.class);
	
	@Autowired
	PrimarySubscriptionService primarySubscriptionService;
	
	@Autowired
	SecondarySubscriptionService secondarySubscriptionService;
		
	@Autowired
	RbtClientService rbtClientService ;
	
	@Value("${referralchain.ringback.subscription.expired.period.in.days}")
	private int expiredPeriodInDays;
	
	@PostMapping("/{pmsisdn}")
	public StatusResponseDto registerPrimarySubscription(
			@PathVariable(value="pmsisdn") String pmsisdn ,
			@RequestParam(value="mode", required= true) String mode,
			@RequestParam(value="operator", required= true) String operator ,
			@RequestParam(value="circle", required= true) String circle,
			@RequestParam(value="catalog_subscription_external_id", required= true) String externalId,
			@RequestParam(value="type", required=true) SubscriptionType type) {
		
		
		logger.debug("registerPrimarySubscription API starts for Msisdn : {} ",pmsisdn);
		logger.debug("checking msisdn : {} is an active PrimarySubscriber",pmsisdn);
		// Check If PMSISDN already exist as an active PrimarySubscriber
		PrimarySubscription existingPsub = primarySubscriptionService.getPrimarySubscriptionByMsisdn(pmsisdn) ;
	
		if(existingPsub!=null && 
				(existingPsub.getUserStatus() == UserStatus.ACTIVE
				|| existingPsub.getUserStatus() == UserStatus.SUSPENDED)) {
			logger.error("msisdn : {} has a Primary Subscription",pmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.PARENT_ALREADY_REGISTERED,
					"Parent has a Primary Subscription");
		} else if(existingPsub!=null && (existingPsub.getUserStatus() == UserStatus.PENDING)) {
	         existingPsub.setPmsisdn(pmsisdn);
	         existingPsub.setUserStatus(UserStatus.PENDING);
	         existingPsub.setCreated(new Date());
	         existingPsub.setLastUpdated(new Date());
	         existingPsub.setTotalCount(-1);
	         existingPsub.setCountLeft(-1);
	         existingPsub.setMode(mode);
	         existingPsub.setOperator(operator);
	         existingPsub.setCircle(circle);
	         existingPsub.setExternalId(externalId);
	         existingPsub.setType(type.name());
	         primarySubscriptionService.updatePrimarySubscription(existingPsub);
	         logger.debug("msisdn : {} already had a pending subscription , overwriting old entry for same msisdn", pmsisdn);
	         return new StatusResponseDto(StatusResponseDto.ResponseStatus.SUCCESS);
	      } 
		
		
		
		logger.debug("msisdn : {} is not a PrimarySubscriber  so checking if msisdn has an active SecondarySubscription or not ",pmsisdn);
		// Check If PMSISDN already exist as an active SecondarySubscriber
		SecondarySubscription existingSsub ;
		existingSsub = secondarySubscriptionService.getSecondarySubscriptionByMsisdn(pmsisdn);
		
		if(existingSsub!=null && (existingSsub.getUserStatus()==UserStatus.ACTIVE)) {
			logger.error("msisdn : {} has an active Secondary Subscription with userStatus : {}  ",pmsisdn,UserStatus.ACTIVE);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.PARENT_ALREADY_REGISTERED,
					"Parent has an active Secondary Subscription");
		}
		logger.debug("msisdn : {} is neither a PrimarySubscriber nor an active SecondarySubscriber so Prepare Primary Subscription to add",pmsisdn);		
		// If above validations are successful , Prepare Primary Subscription to Add
		PrimarySubscription newPsub = new PrimarySubscription() ;
		newPsub.setPmsisdn(pmsisdn);
		newPsub.setUserStatus(UserStatus.PENDING);
		newPsub.setCreated(new Date());
		newPsub.setLastUpdated(new Date());
		newPsub.setTotalCount(-1);
		newPsub.setCountLeft(-1);
		newPsub.setMode(mode);
		newPsub.setOperator(operator);
		newPsub.setCircle(circle);
		newPsub.setExternalId(externalId);
		newPsub.setType(type.name());
		
		// If MSISDN is one of the Child Subscriber with Status as PENDING
		logger.debug("checking if msisdn : {} is one of the Child Subscriber with Status as PENDING",pmsisdn);
		if(existingSsub!=null && (existingSsub.getUserStatus()==UserStatus.PENDING)) {
			// Delete Existing Child Subscription , Update Current Parent Left Count
			// Add New Parent Registration for Existing Child
			logger.debug("msisdn : {} is one of the Child Subscriber with Status as PENDING so "
					+ "Deleting Existing Child Subscription and Updating "
					+ "Current Parent Left Count and Adding a new  Parent Registration for Existing Child");
			primarySubscriptionService.deleteChildUpdateParentAddNewParent(existingSsub,newPsub);
			return new StatusResponseDto(StatusResponseDto.ResponseStatus.SUCCESS);
		}
		logger.debug("msisdn : {} is not a Child Subscriber with Status as PENDING so persist Primary Subscription",pmsisdn);
		// If MSISDN is a New User simply persist Primary Subscription
		primarySubscriptionService.addPrimarySubscription(newPsub) ;
		return new StatusResponseDto(StatusResponseDto.ResponseStatus.SUCCESS);
	}
	
	@GetMapping("/{pmsisdn}")
	public PrimarySubscriptionResponseDto getPrimarySubscriptionInfo(
			@PathVariable(value="pmsisdn") String pmsisdn ,
			@RequestParam(value="mode", required=true) String mode) {
		
		logger.debug("getPrimarySubscriptionInfo API starts for Msisdn : {} ",pmsisdn);
		PrimarySubscription ps = primarySubscriptionService.getPrimarySubscriptionByMsisdn(pmsisdn) ;
		
		if(ps==null) {
			logger.error("PrimarySubscription is not exist for Parent msisdn : {} means Parent msisdn : {} is not registered",pmsisdn,pmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.PARENT_SUBSCRIPTION_NOT_FOUND,
					"Parent is not registered");
		}
		logger.debug("PrimarySubscription is exist for Parent msisdn : {} so preparing Response Dto",pmsisdn);
		return preparePrimarySubscriptionDto(ps);	
	}
	
	public PrimarySubscriptionResponseDto preparePrimarySubscriptionDto(PrimarySubscription ps) {
		
		List<SecondarySubscription> childs = ps.getChilds() ;
		List<SecondarySubscriptionResponseDto> childList = new ArrayList<SecondarySubscriptionResponseDto>();
		
		for(SecondarySubscription ss : childs) {
			childList.add(prepareSecondarySubscriptionDto(ss));
		}
		
		PrimarySubscriptionResponseDto psDto = new PrimarySubscriptionResponseDto();
		psDto.setMsisdn(ps.getPmsisdn());
		psDto.setUserStatus(ps.getUserStatus());
		psDto.setOperator(ps.getOperator());
		psDto.setCircle(ps.getCircle());
		psDto.setTotalCount(ps.getTotalCount());
		psDto.setCountLeft(ps.getCountLeft());
		psDto.setExternalId(ps.getExternalId());
		psDto.setChilds(childList);
		return psDto ;
	}
	
	public SecondarySubscriptionResponseDto prepareSecondarySubscriptionDto(SecondarySubscription ss) {
		
		SecondarySubscriptionResponseDto ssDto = new SecondarySubscriptionResponseDto();
		ssDto.setMsisdn(ss.getCmsisdn());
		ssDto.setUserStatus(ss.getUserStatus());
		ssDto.setStatus(ss.getStatus());
		ssDto.setExpired(ss.getExpired());
		ssDto.setExternalId(ss.getExternalId());
		return ssDto ;
	}
			
	@PostMapping("/{pmsisdn}/child/{cmsisdn}")
	public StatusResponseDto addSecondarySubscription(
			@PathVariable(value="pmsisdn") String pmsisdn ,
			@PathVariable(value="cmsisdn") String cmsisdn ,
			@RequestParam(value="mode", required= true) String mode,
			@RequestParam(value="operator", required= true) String operator ,
			@RequestParam(value="circle", required= true) String circle ,
			@RequestParam(value="provider_name", required= true) String providerName ,
			@RequestParam(value="type", required=true) SubscriptionType type) {
		
		logger.debug("addSecondarySubscription API starts for parent Msisdn : {} and child msisdn : {} ",pmsisdn,cmsisdn);		
		// Check If pmsisdn has active ParentSubscription
		PrimarySubscription ps = primarySubscriptionService.getPrimarySubscriptionByMsisdnAndStatus(pmsisdn,UserStatus.ACTIVE);
		
		if(ps==null) {
			logger.error("parent msisdn : {} is not registered",pmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.PARENT_SUBSCRIPTION_NOT_FOUND,
					"Parent is not registered");
		} 
		
		// Check If pmsisdn has enough count_left for adding another child
		if(ps.getCountLeft() <= 0) {
			logger.error("parent msisdn : {} doesn't have enough count left : {} so throwing an CHILD_LIMIT_EXCEEDED Exception", pmsisdn,ps.getCountLeft());
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_LIMIT_EXCEEDED,
					"Parent Subscription's child limit exceeded");
		}
		
		// Check If cmsisdn already exist as an active or pending parent
		PrimarySubscription existingPsub = primarySubscriptionService.getPrimarySubscriptionByMsisdn(cmsisdn);
		if((existingPsub !=null) &&
			(existingPsub.getUserStatus() == UserStatus.PENDING
			|| existingPsub.getUserStatus() == UserStatus.ACTIVE)) {
			logger.error("child msisdn : {} has an active/pending parent subscription",cmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_ALREADY_REGISTER_AS_PARENT,
					"Child already has an active/pending parent subscription");
		}
		
		// Check If cmsisdn already exist as an active or pending child
		SecondarySubscription existingSsub = secondarySubscriptionService.getSecondarySubscriptionByMsisdn(cmsisdn);
		if((existingSsub!=null)) {
			if (existingSsub.getUserStatus() == UserStatus.PENDING
					|| existingSsub.getUserStatus() == UserStatus.ACTIVE) {
				if(ps.getParentId() == existingSsub.getParentSubscriptionId()) {
					logger.error("child msisdn : {} has an active/pending child subscription with same parent",cmsisdn);
					throw new ReferralChainServiceException(
						ErrorCode.REFERRAL_SERVICE_ERROR,
						ErrorCode.CHILD_ALREADY_ADDED,
						"Child already has an active/pending child subscription with same parent");
				} else {
					logger.error("child msisdn : {} has an active/pending child subscription with different parent",cmsisdn);
					throw new ReferralChainServiceException(
						ErrorCode.REFERRAL_SERVICE_ERROR,
						ErrorCode.CHILD_ALREADY_ADDED_WITH_DIFFERENT_PARENT,
						"Child already has an active/pending child subscription with different parent");
				}
			}
		}
		
		// Fetch RBT Subscription Info for CMSISDN
		RbtSubscriptionResponseDto  rbtSubscription = rbtClientService.getRbtSubscriptionInfo(cmsisdn, providerName);
		// Check if cmsisdn have an active RBT Subscription or if it is a BlackListed user
		if (rbtSubscription.isBlacklisted()== true) {
			logger.error("child msisdn : {} is a blacklisted user",cmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_INVALID,
					"Child is a blacklisted user");
		} else if(rbtSubscription.getStatus().equalsIgnoreCase("ACTIVE")) {
			logger.error("child msisdn : {} already has rbt subscription",cmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_ACTIVE,
					"Child already has rbt subscription");
		}
		
		// If Above Validations are Successful Add Child
		SecondarySubscription ss = new SecondarySubscription() ;
		ss.setCmsisdn(cmsisdn);
		ss.setParentSubscription(ps);
		ss.setPmsisdn(ps.getPmsisdn());
		ss.setpExternalId(ps.getExternalId());
		ss.setUserStatus(UserStatus.PENDING);
		ss.setStatus(Status.PENDING);
		ss.setType(type.name());
		ss.setMode(mode);
		ss.setCreated(new Date());
		ss.setLastUpdated(new Date());
		ss.setProviderName(providerName);
		ss.setExpired(resolveExpiryDateFromConfiguration(new Date(),expiredPeriodInDays));
		logger.debug("adding a child msisdn : {} as a secondary subscription with user status : {} , status : {} ",cmsisdn,UserStatus.PENDING,Status.PENDING);
		secondarySubscriptionService.addSecondarySubscription(ss) ;
		logger.debug("child msisdn : {}  added as secondary subscription",cmsisdn);
		return new StatusResponseDto(StatusResponseDto.ResponseStatus.SUCCESS);
	}
	
	private Date resolveExpiryDateFromConfiguration(Date date, int expiredPeriodInDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH,expiredPeriodInDays);
		return cal.getTime();
	}

	@DeleteMapping("/{pmsisdn}/child/{cmsisdn}")
	public StatusResponseDto removeSecondarySubscription(
			@PathVariable(value="pmsisdn") String pmsisdn ,
			@PathVariable(value="cmsisdn") String cmsisdn ,
			@RequestParam(value="mode", required= true) String mode) {
		
		logger.debug("removeSecondarySubscription API Starts for Parent msisdn  : {} and Child msisdn  : {}",pmsisdn,cmsisdn);
		// Fetch Active Primary Subscription Details
		PrimarySubscription existingPsub = primarySubscriptionService.getPrimarySubscriptionByMsisdnAndStatus(pmsisdn,UserStatus.ACTIVE);
		
		// Check If Active Primary Subscription Exist
		if(existingPsub==null) {
			logger.error("Parent is not registered for child msisdn : {} ",cmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.PARENT_SUBSCRIPTION_NOT_FOUND,
					"Parent is not registered");
		}
		logger.debug("Active Parent msisdn : {} exist" , pmsisdn);
		// Check If cmsisdn already exist as an active or pending child
		SecondarySubscription existingSsub = secondarySubscriptionService.getSecondarySubscriptionByMsisdn(cmsisdn);
		
		if(existingSsub== null) {
			logger.error("Child msisdn is already removed : {} against the parent msisdn : {} ",cmsisdn,existingPsub.getPmsisdn());
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_NOT_FOUND,
					"Child is already removed");
		}
		
		// If Child Belongs to the same parent
		if(existingSsub.getParentSubscriptionId() == existingPsub.getParentId()) {
			if(existingSsub.getUserStatus() == UserStatus.PENDING) {
				logger.debug("child msisdn : {} has Pending Status so trying to delete SecondarySubscription and updating count left for parent : {} ",existingSsub.getCmsisdn(),existingPsub.getPmsisdn());
				secondarySubscriptionService.deleteSecondarySubscriptionAndUpdateCountLeft(existingSsub,Action.REMOVE_CHILD);
			} else if(existingSsub.getUserStatus() == UserStatus.ACTIVE) {
				// Call RBT DeActivation API
				logger.debug("child msisdn : {} has Active Status so hitting rbtDeActivation API agains parent msisdn : {} ",cmsisdn,existingPsub.getPmsisdn());
				String rbtDeActivationStatus = rbtClientService.DeactivateRbtSubscription(existingSsub.getCmsisdn(),existingSsub.getProviderName(),existingSsub.getMode());
				if(rbtDeActivationStatus.equalsIgnoreCase("deactivationpending")) {
					logger.debug("rbtDeActivationStatus is : {} for child msisdn : {} against parent msisdn : {} ",rbtDeActivationStatus,cmsisdn,existingPsub.getPmsisdn());
					secondarySubscriptionService.deleteSecondarySubscriptionAndUpdateCountLeft(existingSsub,Action.REMOVE_CHILD);
				}	
			}
		} else {
			logger.error("Child msisdn : {}  is already removed by this parent msisdn : {} ",cmsisdn , pmsisdn);
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.CHILD_ALREADY_REMOVED,
					"Child is already removed by this parent, now child belongs to other parent");
		}
		
		return new StatusResponseDto(StatusResponseDto.ResponseStatus.SUCCESS);
	}
}