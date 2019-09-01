package com.onmobile.vol.referralchain.app.rest.service;

import java.util.Date;
import java.util.List;

import com.onmobile.vol.referralchain.app.dataaccess.domain.Action;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dto.SecondarySubscription;

public interface SecondarySubscriptionService {
		
	public SecondarySubscription addSecondarySubscription(SecondarySubscription ssubscription);
	
	public SecondarySubscription getSecondarySubscriptionByMsisdn(String cmsisdn);
	
	public void deleteSecondarySubscription(SecondarySubscription ssubscription);

	public void deleteSecondarySubscriptionAndUpdateCountLeft(SecondarySubscription existingSsub,Action action);
	
	public void updateSecondarySubscriptionForSMSStatus(SecondarySubscriptionDom updatedSsub, SMSStatus smsStatus, Action action);
	
	public void updateSecondarySubscriptionForGiftAccept(SecondarySubscription existingSsub);
	
	public Date resolveNextRetryTime(int nextRetryTimeInMinutes) ;
	
	public List<SecondarySubscriptionDom> getAllSMSNotificationCandidates(SMSStatus smsStatus , Date currentTime);
	
	public List<SecondarySubscriptionDom> getSecondarySubscriptionsByBatch(int batchSize, int lastChildId);
	
	public Long getSecondarySubscriptionCount();
}