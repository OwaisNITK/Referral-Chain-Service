package com.onmobile.vol.referralchain.app.rest.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.onmobile.vol.referralchain.app.client.ump.SMSNotificationService;
import com.onmobile.vol.referralchain.app.dataaccess.dao.PrimarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.dao.SecondarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.dao.TransactionHistoryDao;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Action;
import com.onmobile.vol.referralchain.app.dataaccess.domain.PrimarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Status;
import com.onmobile.vol.referralchain.app.dataaccess.domain.TransactionHistory;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;
import com.onmobile.vol.referralchain.app.dto.DtoDomConversionService;
import com.onmobile.vol.referralchain.app.dto.SecondarySubscription;
import com.onmobile.vol.referralchain.app.rest.service.SecondarySubscriptionService;
import com.onmobile.vol.referralchain.app.utils.CryptoUtil;

@Component
public class SecondarySubscriptionServiceImpl implements SecondarySubscriptionService {
	
	@Autowired
	PrimarySubscriptionDao primarySubscriptionDao;
	
	@Autowired
	SecondarySubscriptionDao secondarySubscriptionDao ;
	
	@Autowired
	DtoDomConversionService dtoDomConverter ;
	
	@Autowired
	CryptoUtil cryptoUtil ;
	
	@Autowired
	TransactionHistoryDao transactionHistoryDao ;

	@Autowired
	private SMSNotificationService smsNotificationService ;
	
	@Value("${referralchain.sms.notification.retry.count}")
	private int retryCount;
	
	@Value("${referralchain.sms-notification.next.retry.time.in.minutes}")
	private int nextRetryTimeInMinutes;
	
	
	@Override
	@Transactional
	public SecondarySubscription addSecondarySubscription(SecondarySubscription ss) {
				
		SMSStatus smsStatus = smsNotificationService.sendActivationSMS(ss.getPmsisdn(),ss.getCmsisdn(),ss.getProviderName());
		
		if(smsStatus.equals(SMSStatus.SENT)) {
			ss.setSmsStatus(SMSStatus.SENT);
			ss.setNextRetryTime(null);
			ss.setSmsRetryCountLeft(0);
		} else if (smsStatus.equals(SMSStatus.PENDING)) {
			ss.setSmsStatus(SMSStatus.PENDING);
			ss.setSmsRetryCountLeft(retryCount);
			ss.setNextRetryTime(resolveNextRetryTime(nextRetryTimeInMinutes));
		}
		
		// Adding New Child Subscription
		SecondarySubscriptionDom newSsub = secondarySubscriptionDao.addSecondarySubscription(dtoDomConverter.createSecondarySubscriptionDomFromDto(ss));
				
		// Decrease LeftCount For Parent Subscription
		PrimarySubscriptionDom currentParent = newSsub.getParentSubscriptionDom();
		currentParent.setCountLeft(currentParent.getCountLeft()-1);
		primarySubscriptionDao.updatePrimarySubscription(currentParent);
		
		// Update Transaction History
		TransactionHistory tdetails = new TransactionHistory();
		tdetails.setCreated(new Date());
		tdetails.setPmsisdn(newSsub.getParentSubscriptionDom().getPmsisdn());
		tdetails.setCmsisdn(newSsub.getCmsisdn());
		tdetails.setAction(Action.ADD_CHILD);
		tdetails.setInfo("Primary Subscriber had Added A Child");
		tdetails.setMode(newSsub.getMode());
		transactionHistoryDao.addTransactionHistory(tdetails);
				
		return dtoDomConverter.createSecondarySubscriptionFromDom(newSsub) ;
	}
	
	@Override
	public Date resolveNextRetryTime(int nextRetryTimeInMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE,nextRetryTimeInMinutes);
		return cal.getTime();
	}

	@Override
	public SecondarySubscription getSecondarySubscriptionByMsisdn(String cmsisdn) {
		SecondarySubscriptionDom ssDom = secondarySubscriptionDao.getSecondarySubscriptionByMsisdn(cryptoUtil.encrypt(cmsisdn));
		
		if(ssDom!=null) {
			return dtoDomConverter.createSecondarySubscriptionFromDom(ssDom);
		}
		
		return null ;
	}

	@Override
	@Transactional
	public void deleteSecondarySubscription(SecondarySubscription ssubscription) {
		secondarySubscriptionDao.deleteSecondarySubscriptionById(ssubscription.getChildId());
		
		// Update TransactionHistory
		TransactionHistory tdetails = new TransactionHistory();
		tdetails.setCreated(new Date());
		tdetails.setPmsisdn(ssubscription.getPmsisdn());
		tdetails.setCmsisdn(ssubscription.getCmsisdn());
		tdetails.setAction(Action.REMOVE_CHILD);
		tdetails.setInfo(" Child has been deleted");
		tdetails.setMode(ssubscription.getMode());
		transactionHistoryDao.addTransactionHistory(tdetails);
	}

	@Override
	@Transactional
	public void deleteSecondarySubscriptionAndUpdateCountLeft(SecondarySubscription existingSsub,Action action) {
		
		// Increase LeftCount For Parent Subscription
		SecondarySubscriptionDom ssDom = secondarySubscriptionDao.getSecondarySubscriptionByMsisdn(cryptoUtil.encrypt(existingSsub.getCmsisdn()));
		PrimarySubscriptionDom existingPsDom = ssDom.getParentSubscriptionDom();
		existingPsDom.setCountLeft(existingPsDom.getCountLeft()+1);
		primarySubscriptionDao.updatePrimarySubscription(existingPsDom);
		
		// Delete Secondary Subscription
		secondarySubscriptionDao.deleteSecondarySubscription(ssDom);
		
		// Update TransactionHistory
		TransactionHistory tdetails = new TransactionHistory();
		tdetails.setCreated(new Date());
		tdetails.setPmsisdn(existingPsDom.getPmsisdn());
		tdetails.setCmsisdn(cryptoUtil.encrypt(existingSsub.getCmsisdn()));
		tdetails.setAction(action);
		tdetails.setMode(existingSsub.getMode());
		
		if(action == Action.CHILD_REJECT) {
			tdetails.setInfo("Child has rejected the subscription");
		} else if (action == Action.REMOVE_CHILD) {
			tdetails.setInfo("Parent has removed Child");
		} else if (action == Action.CHILD_EXPIRED ) {
			tdetails.setInfo("Child has expired , removed by daemon");
		}
		
		transactionHistoryDao.addTransactionHistory(tdetails);
	}

	@Override
	public List<SecondarySubscriptionDom> getAllSMSNotificationCandidates(SMSStatus smsStatus , Date currentTime) {
		return secondarySubscriptionDao.getAllSMSNotificationCandidates(smsStatus, currentTime);
	}

	@Override
	@Transactional
	public void updateSecondarySubscriptionForSMSStatus(SecondarySubscriptionDom updatedSsub, SMSStatus smsStatus, Action action) {
		// Update Secondary Subscription
		secondarySubscriptionDao.updateSecondarySubscription(updatedSsub);
		
		if(smsStatus == SMSStatus.SENT) {
			// Update TransactionHistory
			TransactionHistory tdetails = new TransactionHistory();
			tdetails.setCreated(new Date());
			tdetails.setPmsisdn(updatedSsub.getParentSubscriptionDom().getPmsisdn());
			tdetails.setCmsisdn(updatedSsub.getCmsisdn());
			tdetails.setAction(action);
			tdetails.setInfo("Activation Link SMS Successfully Sent to Child MSISDN");
			tdetails.setMode(updatedSsub.getMode());
			transactionHistoryDao.addTransactionHistory(tdetails);
		}
	}

	@Override
	public List<SecondarySubscriptionDom> getSecondarySubscriptionsByBatch(int batchSize, int lastChildId) {
		return secondarySubscriptionDao.getSecondarySubscriptionsByBatch(batchSize,lastChildId);
	}

	@Override
	public Long getSecondarySubscriptionCount() {
		return secondarySubscriptionDao.getSecondarySubscriptionCount();
	}

	@Override
	@Transactional
	public void updateSecondarySubscriptionForGiftAccept(SecondarySubscription existingSsub) {
		
		SecondarySubscriptionDom ssDom = secondarySubscriptionDao.getSecondarySubscriptionByMsisdn(cryptoUtil.encrypt(existingSsub.getCmsisdn()));
		ssDom.setStatus(Status.ACCEPTED);
		ssDom.setExpired(null);
		secondarySubscriptionDao.updateSecondarySubscription(ssDom);
		
		// Update TransactionHistory
		TransactionHistory tdetails = new TransactionHistory();
		tdetails.setCreated(new Date());
		tdetails.setPmsisdn(ssDom.getParentSubscriptionDom().getPmsisdn());
		tdetails.setCmsisdn(ssDom.getCmsisdn());
		tdetails.setAction(Action.CHILD_ACCEPTED);
		tdetails.setInfo("Child MSISDN has accepted the Gift");
		tdetails.setMode(existingSsub.getMode());
		transactionHistoryDao.addTransactionHistory(tdetails);
		
	}

	//Setters Define To Inject Mock Dao Layer Instance for JUnit
	public void setPrimarySubscriptionDao(PrimarySubscriptionDao primarySubscriptionDao) {
		this.primarySubscriptionDao = primarySubscriptionDao;
	}

	public void setSecondarySubscriptionDao(SecondarySubscriptionDao secondarySubscriptionDao) {
		this.secondarySubscriptionDao = secondarySubscriptionDao;
	}

	public void setTransactionHistoryDao(TransactionHistoryDao transactionHistoryDao) {
		this.transactionHistoryDao = transactionHistoryDao;
	}

	public void setSmsNotificationService(SMSNotificationService smsNotificationService) {
		this.smsNotificationService = smsNotificationService;
	}		
}