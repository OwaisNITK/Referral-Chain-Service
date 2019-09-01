package com.onmobile.vol.referralchain.app.rest.service.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.onmobile.vol.referralchain.app.dataaccess.dao.PrimarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.dao.SecondarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.dao.TransactionHistoryDao;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Action;
import com.onmobile.vol.referralchain.app.dataaccess.domain.PrimarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.TransactionHistory;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;
import com.onmobile.vol.referralchain.app.dto.DtoDomConversionService;
import com.onmobile.vol.referralchain.app.dto.PrimarySubscription;
import com.onmobile.vol.referralchain.app.dto.SecondarySubscription;
import com.onmobile.vol.referralchain.app.rest.service.PrimarySubscriptionService;
import com.onmobile.vol.referralchain.app.utils.CryptoUtil;

@Component
public class PrimarySubscriptionServiceImpl implements PrimarySubscriptionService {
	
	public static Logger logger  = LogManager.getLogger(PrimarySubscriptionServiceImpl.class);
	
	@Autowired
	PrimarySubscriptionDao primarySubscriptionDao ;
	
	@Autowired
	SecondarySubscriptionDao secondarySubscriptionDao ;
	
	@Autowired
	TransactionHistoryDao transactionHistoryDao ;
	
	@Autowired
	DtoDomConversionService dtoDomConverter ;
	
	@Autowired
	CryptoUtil cryptoUtil ;

	@Override
	@Transactional
	public PrimarySubscription addPrimarySubscription(PrimarySubscription ps) {
		PrimarySubscriptionDom psDom = dtoDomConverter.createPrimarySubscriptionDomFromDto(ps);
		PrimarySubscriptionDom updatedPsDom = primarySubscriptionDao.addPrimarySubscription(psDom);
		
		// Update Transaction History
		TransactionHistory tdetails = new TransactionHistory();
		tdetails.setCreated(new Date());
		tdetails.setPmsisdn(updatedPsDom.getPmsisdn());
		tdetails.setAction(Action.PARENT_REGISTER);
		tdetails.setInfo("Registered Primary Subscriber");
		tdetails.setMode(updatedPsDom.getMode());
		transactionHistoryDao.addTransactionHistory(tdetails);
		
		return dtoDomConverter.createPrimarySubscriptionFromDom(updatedPsDom) ;
	}
	
	@Override
	@Transactional
	public PrimarySubscription updatePrimarySubscription(PrimarySubscription ps) {
		PrimarySubscriptionDom psDom = dtoDomConverter.createPrimarySubscriptionDomFromDto(ps);
		PrimarySubscriptionDom updatedPsDom = primarySubscriptionDao.updatePrimarySubscription(psDom);
		return dtoDomConverter.createPrimarySubscriptionFromDom(updatedPsDom) ;
	}

	@Override
	public PrimarySubscription getPrimarySubscriptionByMsisdnAndStatus(String msisdn, UserStatus userStatus) {
		PrimarySubscriptionDom existingPsDom = primarySubscriptionDao.getPrimarySubscriptionByMsisdnAndStatus(cryptoUtil.encrypt(msisdn),userStatus);
		
		if(existingPsDom!=null) {
			return dtoDomConverter.createPrimarySubscriptionFromDom(existingPsDom);
		}
		
		return null ;
	}

	@Override
	public PrimarySubscription getPrimarySubscriptionByMsisdn(String pmsisdn) {
		PrimarySubscriptionDom existingPsDom = primarySubscriptionDao.getPrimarySubscriptionByMsisdn(cryptoUtil.encrypt(pmsisdn));
		
		if(existingPsDom!=null) {
			return dtoDomConverter.createPrimarySubscriptionFromDom(existingPsDom);
		}
		
		return null ;
	}

	@Override
	@Transactional
	public void deleteChildUpdateParentAddNewParent(SecondarySubscription existingSsub, PrimarySubscription newPsub) {
		
		// Update Current Parent Left Count
		SecondarySubscriptionDom existingSsubDom = secondarySubscriptionDao.getSecondarySubscriptionByMsisdn(cryptoUtil.encrypt(existingSsub.getCmsisdn()));
		PrimarySubscriptionDom currentParent = existingSsubDom.getParentSubscriptionDom();
		currentParent.setCountLeft(currentParent.getCountLeft()+1);
		
		logger.debug("for child msisdn  : {} Updating Current Parent msisdn  : {} and  with Left Count : {} ",existingSsub.getCmsisdn(),currentParent.getPmsisdn(),currentParent.getCountLeft()-1);
		primarySubscriptionDao.addPrimarySubscription(currentParent);
		logger.debug("for child msisdn : {} Parent msisdn  : {} updated  with Left Count : {}",existingSsub.getCmsisdn(),currentParent.getPmsisdn(),currentParent.getCountLeft());		
		
		// Delete Child Subscription and Update Transaction History
		logger.debug("for child msisdn : {} trying to delete an Child Subscription and Updating Transaction History");
		secondarySubscriptionDao.deleteSecondarySubscriptionById(existingSsub.getChildId());
		
		// Update TransactionHistory For Child Deleted
		TransactionHistory tdetails = new TransactionHistory();
		tdetails.setCreated(new Date());
		tdetails.setPmsisdn(currentParent.getPmsisdn());
		tdetails.setCmsisdn(existingSsubDom.getCmsisdn());
		tdetails.setAction(Action.CHILD_REJECT);
		tdetails.setInfo("ChildId MSISDN has subscribed for Parent Subscription , Hence Removed");
		tdetails.setMode(currentParent.getMode());
		transactionHistoryDao.addTransactionHistory(tdetails);
		logger.debug("transcation history is updated for parent msisdn : {} and child msisdn : {}  for action : {} ",currentParent.getPmsisdn(),existingSsub.getCmsisdn(),Action.CHILD_REJECT );
		
		// Add New Parent Subscription
		logger.debug("for msisdn : {} adding a new Parent Subscription",newPsub.getPmsisdn());
		PrimarySubscriptionDom newPsDom = primarySubscriptionDao.addPrimarySubscription(dtoDomConverter.createPrimarySubscriptionDomFromDto(newPsub));
		logger.debug("for msisdn : {} Parent Subscription added now updating TransactionHistory ",newPsub.getPmsisdn());
		
		// Update TransactionHistory For New Parent Subscription
		TransactionHistory newtdetails = new TransactionHistory();
		newtdetails.setCreated(new Date());
		newtdetails.setPmsisdn(newPsDom.getPmsisdn());
		newtdetails.setAction(Action.PARENT_REGISTER);
		newtdetails.setInfo("Registered Pending Child As Primary Subscriber");
		newtdetails.setMode(newPsub.getMode());
		transactionHistoryDao.addTransactionHistory(newtdetails);
		logger.debug("transcation history is updated for parent msisdn : {} for action : {} ",newPsub.getPmsisdn(),Action.PARENT_REGISTER );
	}

	//Setters Define To Inject Mock Dao Layer Instance for JUnit
	public void setPrimarySubscriptionDao(PrimarySubscriptionDao primarySubscriptionDao) {
		this.primarySubscriptionDao = primarySubscriptionDao ;
	}

	public void setSecondarySubscriptionDao(SecondarySubscriptionDao secondarySubscriptionDao) {
		this.secondarySubscriptionDao = secondarySubscriptionDao;
	}

	public void setTransactionHistoryDao(TransactionHistoryDao transactionHistoryDao) {
		this.transactionHistoryDao = transactionHistoryDao;
	}
}