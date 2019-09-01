package com.onmobile.vol.referralchain.app.dataaccess.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.dataaccess.dao.SecondarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.repository.SecondarySubscriptionRepository;
import com.onmobile.vol.referralchain.app.utils.OffsetBasedPageRequest;

@Component
public class SecondarySubscriptionDaoImpl implements SecondarySubscriptionDao {
	
	@Autowired
	SecondarySubscriptionRepository secondarySubscriptionRepository ;
	
	@Override
	public SecondarySubscriptionDom addSecondarySubscription(SecondarySubscriptionDom ssubscription) {
		return secondarySubscriptionRepository.save(ssubscription);
	}

	@Override
	public SecondarySubscriptionDom getSecondarySubscriptionByMsisdn(String cmsisdn) {
		return secondarySubscriptionRepository.getSecondarySubscriptionByMsisdn(cmsisdn);
	}

	@Override
	public void deleteSecondarySubscription(SecondarySubscriptionDom ss) {
		secondarySubscriptionRepository.delete(ss);
	}

	@Override
	public List<SecondarySubscriptionDom> getAllSMSNotificationCandidates(SMSStatus smsStatus , Date currentTime) {
		return secondarySubscriptionRepository.getAllSMSNotificationCandidates(smsStatus,currentTime);
	}

	@Override
	public SecondarySubscriptionDom updateSecondarySubscription(SecondarySubscriptionDom ss) {
		return secondarySubscriptionRepository.save(ss);
	}

	@Override
	public List<SecondarySubscriptionDom> getSecondarySubscriptionsByBatch(int batchSize, int lastChildId) {
		return secondarySubscriptionRepository.getSecondarySubscriptionsByBatch(lastChildId, OffsetBasedPageRequest.of(0, batchSize,new Sort(Sort.Direction.ASC, "childId")));
	}

	@Override
	public Long getSecondarySubscriptionCount() {
		return secondarySubscriptionRepository.count();
	}

	@Override
	public void deleteSecondarySubscriptionById(int id) {
		secondarySubscriptionRepository.deleteById(id);		
	}
}