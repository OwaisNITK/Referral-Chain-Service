package com.onmobile.vol.referralchain.app.rest.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.onmobile.vol.referralchain.app.config.ExternalIdMappingConfig;
import com.onmobile.vol.referralchain.app.dataaccess.dao.PrimarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.dao.SecondarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.dao.TransactionHistoryDao;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Action;
import com.onmobile.vol.referralchain.app.dataaccess.domain.PrimarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Status;
import com.onmobile.vol.referralchain.app.dataaccess.domain.TransactionHistory;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;
import com.onmobile.vol.referralchain.app.dto.PrismCallBackRequestDto;
import com.onmobile.vol.referralchain.app.dto.StatusResponseDto.ResponseStatus;
import com.onmobile.vol.referralchain.app.rest.service.PrismCallbackService;
import com.onmobile.vol.referralchain.app.utils.CryptoUtil;

@Component
public class PrismCallbackServiceImpl implements PrismCallbackService {

	public static final String useCaseSeperator = "_";

	private static Logger logger = LogManager.getLogger(PrismCallbackServiceImpl.class);

	@Autowired
	PrimarySubscriptionDao primarySubscriptionDao;

	@Autowired
	SecondarySubscriptionDao secondarySubscriptionDao;

	@Autowired
	TransactionHistoryDao transactionHistoryDao;

	@Autowired
	ExternalIdMappingConfig externalIdMap;
	
	@Autowired
	CryptoUtil cryptoUtil ;

	@Override
	public ResponseStatus prismCallBack(PrismCallBackRequestDto prismCallBackRequestDto) {

		String useCase = prismCallBackRequestDto.getReferralType() + useCaseSeperator
				+ prismCallBackRequestDto.getAction() + useCaseSeperator + prismCallBackRequestDto.getStatus();

		Action action = null;
		
		logger.debug("Usecase : {} for prismCallBack for msisdn : {}",useCase ,prismCallBackRequestDto.getMsisdn());

		try {
			action = Action.valueOf(useCase);

			switch (action) {
			case PARENT_ACT_SUCCESS:
				parentActSuccess(prismCallBackRequestDto);
				break;
			case PARENT_ACT_FAILURE:
				parentActFailure(prismCallBackRequestDto);
				break;
			case PARENT_DCT_SUCCESS:
				parentDctSuccess(prismCallBackRequestDto, action);
				break;
			case PARENT_REN_FAILURE:
				parentDctSuccess(prismCallBackRequestDto, action);
				break;
			case PARENT_SUS_SUCCESS:
				parentSusSuccess(prismCallBackRequestDto, action);
				break;
			case PARENT_SUS_FAILURE:
				parentSusSuccess(prismCallBackRequestDto, action);
				break;
			case PARENT_RES_SUCCESS:
		        parentResSuccess(prismCallBackRequestDto, action);
		        break;
		    case PARENT_REN_SUCCESS:
		        parentResSuccess(prismCallBackRequestDto, action);
		        break;
			case CHILD_ACT_SUCCESS:
				childActSuccess(prismCallBackRequestDto);
				break;
			case CHILD_ACT_FAILURE:
				childActFailure(prismCallBackRequestDto, action);
				break;
			case CHILD_DCT_SUCCESS:
				childActFailure(prismCallBackRequestDto, action);
				break;

			default:
				return ResponseStatus.SUCCESS;
			}
		} catch (Exception ex) {
			logger.info("An Exception has been compromised in order to send SUCCESS response to PRISM for msisdn: "
					+ prismCallBackRequestDto.getMsisdn() + "and Action is : " + action
					+ "and exception message is" + ex.getMessage());
		}

		return ResponseStatus.SUCCESS;
	}

	@Transactional
	private ResponseStatus parentActSuccess(PrismCallBackRequestDto prismCallBackRequestDto) {
		String msisdn  = prismCallBackRequestDto.getMsisdn();
		PrimarySubscriptionDom primarySubscription = primarySubscriptionDao
				.getPrimarySubscriptionByMsisdn(cryptoUtil.encrypt(msisdn));
		
		if (primarySubscription != null) {
			logger.debug("primarySubscription exist for msisdn : {} so updating the Subscription",msisdn);
			primarySubscription.setUserStatus(UserStatus.ACTIVE);
			primarySubscription.setExternalId(prismCallBackRequestDto.getExternalId());
			int totalCountFromConfiguration = Integer.parseInt(externalIdMap.getTotalCount(prismCallBackRequestDto.getExternalId()));
			primarySubscription.setTotalCount(totalCountFromConfiguration);
			primarySubscription.setCountLeft(totalCountFromConfiguration);
			primarySubscriptionDao.updatePrimarySubscription(primarySubscription);
			logger.debug("primarySubscription update for msisdn : {} with ExternalId : {} ,Status : {}  and totalCount : {}",msisdn,prismCallBackRequestDto.getExternalId(),UserStatus.ACTIVE,totalCountFromConfiguration);
			
			TransactionHistory tdetails = new TransactionHistory();
			tdetails.setCreated(new Date());
			tdetails.setPmsisdn(primarySubscription.getPmsisdn());
			tdetails.setAction(Action.PARENT_ACT_SUCCESS);
			tdetails.setInfo("Prism " + Action.PARENT_ACT_SUCCESS + " call Success for Parent MSISDN");
			tdetails.setMode(prismCallBackRequestDto.getMode());
			transactionHistoryDao.addTransactionHistory(tdetails);
			logger.debug("transcation is updated for msisdn : "+msisdn+" for action : "+Action.PARENT_ACT_SUCCESS);
		}
		return ResponseStatus.SUCCESS;
	}
	
	@Transactional
	private ResponseStatus parentResSuccess(PrismCallBackRequestDto prismCallBackRequestDto,Action action) {
	      String msisdn  = prismCallBackRequestDto.getMsisdn();
	      PrimarySubscriptionDom psDom = primarySubscriptionDao.getPrimarySubscriptionByMsisdn(cryptoUtil.encrypt(msisdn));
	      if (psDom != null) {
	         logger.debug("primarySubscription is exist for msisdn : {} so updating the Subscription for Action : {}",msisdn,action);
	         psDom.setUserStatus(UserStatus.ACTIVE);
	         primarySubscriptionDao.updatePrimarySubscription(psDom);
	         logger.debug("primarySubscription updated for msisdn : {} with user Status : {} for Action : {}",msisdn,UserStatus.ACTIVE,action);
				
	         TransactionHistory tdetails = new TransactionHistory();
	         tdetails.setCreated(new Date());
	         tdetails.setPmsisdn(psDom.getPmsisdn());
	         tdetails.setAction(action);
	         tdetails.setInfo("Prism " + action + " call Success for Parent MSISDN");
	         tdetails.setMode(prismCallBackRequestDto.getMode());
	         transactionHistoryDao.addTransactionHistory(tdetails);
	         logger.debug("transcation is updated for msisdn : "+msisdn+" for action : "+action);			
	      }
	      return ResponseStatus.SUCCESS;
	   }

	@Transactional
	private ResponseStatus parentActFailure(PrismCallBackRequestDto prismCallBackRequestDto) {
		String msisdn = prismCallBackRequestDto.getMsisdn();
		PrimarySubscriptionDom primarySubscription = primarySubscriptionDao.getPrimarySubscriptionByMsisdn(cryptoUtil.encrypt(msisdn));
		if (primarySubscription != null) {
			
			logger.debug("primarySubscription is exist for msisdn : {} so deleting the Subscription for Action {}: ",msisdn,Action.PARENT_ACT_FAILURE);
			primarySubscriptionDao.deletePrimarySubscription(primarySubscription);
			logger.debug("primarySubscription deleted for msisdn : {} for Action : {} ",msisdn,Action.PARENT_ACT_FAILURE);
			
			TransactionHistory tdetails = new TransactionHistory();
			tdetails.setCreated(new Date());
			tdetails.setPmsisdn(primarySubscription.getPmsisdn());
			tdetails.setAction(Action.PARENT_ACT_FAILURE);
			tdetails.setInfo("Prism " + Action.PARENT_ACT_FAILURE + " call Success for Parent MSISDN");
			tdetails.setMode(prismCallBackRequestDto.getMode());
			transactionHistoryDao.addTransactionHistory(tdetails);
			logger.debug("transcation is updated for msisdn : {} for action : {} ",msisdn,Action.PARENT_ACT_FAILURE);
		}

		return ResponseStatus.SUCCESS;
	}

	@Transactional
	private ResponseStatus parentDctSuccess(PrismCallBackRequestDto prismCallBackRequestDto, Action action) {
		String msisdn  = prismCallBackRequestDto.getMsisdn();
		PrimarySubscriptionDom primarySubscription = primarySubscriptionDao
				.getPrimarySubscriptionByMsisdn(cryptoUtil.encrypt(msisdn));
		if (primarySubscription != null) {
			
			logger.debug("primarySubscription is exist for msisdn : {} so deleting the Subscription for Action : {} ",msisdn,action);
			primarySubscriptionDao.deletePrimarySubscription(primarySubscription);
			logger.debug("primarySubscription deleted for msisdn : {} for Action : {}  ",msisdn,action);
			
			TransactionHistory tdetails = new TransactionHistory();
			tdetails.setCreated(new Date());
			tdetails.setPmsisdn(primarySubscription.getPmsisdn());
			tdetails.setAction(action);
			tdetails.setMode(prismCallBackRequestDto.getMode());
			tdetails.setInfo("Prism " + action + " call is Success for Parent MSISDN");
			transactionHistoryDao.addTransactionHistory(tdetails);
			logger.debug("transcation is updated for msisdn : {} for action : {} ",msisdn,action);
		}
		return ResponseStatus.SUCCESS;
	}

	@Transactional
	private ResponseStatus parentSusSuccess(PrismCallBackRequestDto prismCallBackRequestDto, Action action) {
		String msisdn =prismCallBackRequestDto.getMsisdn();
		PrimarySubscriptionDom primarySubscription = primarySubscriptionDao
				.getPrimarySubscriptionByMsisdn(cryptoUtil.encrypt(msisdn));
		if (primarySubscription != null) {
			logger.debug("primarySubscription is exist for msisdn : {} so updating the Subscription for Action : {}",msisdn,action);
			primarySubscription.setUserStatus(UserStatus.SUSPENDED);
			primarySubscriptionDao.updatePrimarySubscription(primarySubscription);
			logger.debug("primarySubscription updated for msisdn : {} with user Status : {} for Action : {}",msisdn,UserStatus.SUSPENDED,action);
			TransactionHistory tdetails = new TransactionHistory();
			tdetails.setCreated(new Date());
			tdetails.setPmsisdn(primarySubscription.getPmsisdn());
			tdetails.setAction(action);
			tdetails.setMode(prismCallBackRequestDto.getMode());
			tdetails.setInfo("Prism " + action + " call is Success for Parent MSISDN");
			transactionHistoryDao.addTransactionHistory(tdetails);
			logger.debug("transcation is updated for msisdn : {} for action : {} ",msisdn,action);
		}

		return ResponseStatus.SUCCESS;
	}

	@Transactional
	private ResponseStatus childActSuccess(PrismCallBackRequestDto prismCallBackRequestDto) {
		String msisdn = prismCallBackRequestDto.getMsisdn();
		SecondarySubscriptionDom secondarySubscription = secondarySubscriptionDao
				.getSecondarySubscriptionByMsisdn(cryptoUtil.encrypt(msisdn));
		if (secondarySubscription != null) {
			logger.debug("secondarySubscription is exist for msisdn : {} so updating the secondarySubscription for Action : {}",msisdn,Action.CHILD_ACT_SUCCESS);
			String pmsisdnForCurrentChild = secondarySubscription.getParentSubscriptionDom().getPmsisdn();
			secondarySubscription.setUserStatus(UserStatus.ACTIVE);
			secondarySubscription.setStatus(Status.ACCEPTED);
			secondarySubscription.setExternalId(prismCallBackRequestDto.getExternalId());
			secondarySubscription.setExpired(null);
			secondarySubscriptionDao.updateSecondarySubscription(secondarySubscription);
			logger.debug("secondarySubscription updated for msisdn : "+msisdn+" with user Status "+UserStatus.ACTIVE+ " and Statsus : "+Status.ACCEPTED+" for Action : "+Action.CHILD_ACT_SUCCESS);
			TransactionHistory tdetails = new TransactionHistory();
			tdetails.setCreated(new Date());
			tdetails.setPmsisdn(pmsisdnForCurrentChild);
			tdetails.setCmsisdn(secondarySubscription.getCmsisdn());
			tdetails.setAction(Action.CHILD_ACT_SUCCESS);
			tdetails.setInfo("Prism " + Action.CHILD_ACT_SUCCESS + " call Success for Child MSISDN");
			tdetails.setMode(prismCallBackRequestDto.getMode());
			transactionHistoryDao.addTransactionHistory(tdetails);
			logger.debug("transcation is updated for msisdn : {} for action : {}",msisdn,Action.CHILD_ACT_SUCCESS);
		}
		return ResponseStatus.SUCCESS;
	}

	@Transactional
	private ResponseStatus childActFailure(PrismCallBackRequestDto prismCallBackRequestDto, Action action) {
		String msisdn = prismCallBackRequestDto.getMsisdn();
		SecondarySubscriptionDom secondarySubscription = secondarySubscriptionDao
				.getSecondarySubscriptionByMsisdn(cryptoUtil.encrypt(msisdn));
		if (secondarySubscription != null) {
			logger.debug("secondarySubscription is exist for msisdn : {} so deleting the secondarySubscription for Action : {} and updating primarySubscription CountLeft",msisdn,action);
			PrimarySubscriptionDom primarySubscription = secondarySubscription.getParentSubscriptionDom();
			secondarySubscriptionDao.deleteSecondarySubscriptionById(secondarySubscription.getChildId());
			int countLeft = primarySubscription.getCountLeft() + 1;
			primarySubscription.setCountLeft(countLeft);
			primarySubscriptionDao.updatePrimarySubscription(primarySubscription);
			logger.debug("secondarySubscription deleted for msisdn : {} for Action : {} and updated primarySubscription countleft with {} for parent msisdn  : {}" ,msisdn,action,countLeft,primarySubscription.getPmsisdn());
			TransactionHistory tdetails = new TransactionHistory();
			tdetails.setCreated(new Date());
			tdetails.setPmsisdn(primarySubscription.getPmsisdn());
			tdetails.setCmsisdn(secondarySubscription.getCmsisdn());
			tdetails.setAction(action);
			tdetails.setInfo("Prism " + action + " call Success for Child MSISDN");
			tdetails.setMode(prismCallBackRequestDto.getMode());
			transactionHistoryDao.addTransactionHistory(tdetails);
			logger.debug("transcation is updated for msisdn : {}  for action :  {} ",msisdn,action);
		}
		return ResponseStatus.SUCCESS;
	}

}
