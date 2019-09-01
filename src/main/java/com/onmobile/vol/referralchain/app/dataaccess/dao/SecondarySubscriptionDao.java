package com.onmobile.vol.referralchain.app.dataaccess.dao;

import java.util.Date;
import java.util.List;

import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;

public interface SecondarySubscriptionDao {
	
		public SecondarySubscriptionDom addSecondarySubscription(SecondarySubscriptionDom ssubscription) ;

		public SecondarySubscriptionDom getSecondarySubscriptionByMsisdn(String cmsisdn);

		public void deleteSecondarySubscription(SecondarySubscriptionDom ssubscription);
		
		public void deleteSecondarySubscriptionById(int id);

		public List<SecondarySubscriptionDom> getAllSMSNotificationCandidates(SMSStatus smsStatus , Date currentTime);
		
		public SecondarySubscriptionDom updateSecondarySubscription(SecondarySubscriptionDom ss);

		public List<SecondarySubscriptionDom> getSecondarySubscriptionsByBatch(int batchSize, int lastChildId);

		public Long getSecondarySubscriptionCount();
}