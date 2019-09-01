package com.onmobile.vol.referralchain.app.dataaccess.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.dataaccess.dao.PrimarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.domain.PrimarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;
import com.onmobile.vol.referralchain.app.dataaccess.repository.PrimarySubscriptionRespository;

@Component
public class PrimarySubscriptionDaoImpl implements PrimarySubscriptionDao {
	
	@Autowired
	PrimarySubscriptionRespository primarySubscriptionRespository ;
	
	@Override
	public PrimarySubscriptionDom addPrimarySubscription(PrimarySubscriptionDom psubscription) {
		return primarySubscriptionRespository.save(psubscription);
	}
	
	@Override
	public PrimarySubscriptionDom updatePrimarySubscription(PrimarySubscriptionDom psubscription) {
		return primarySubscriptionRespository.save(psubscription);
	}
	
	@Override
	public PrimarySubscriptionDom getPrimarySubscriptionByMsisdnAndStatus(String pmsisdn, UserStatus userStatus) {
		return primarySubscriptionRespository.getPrimarySubscriptionByMsisdnAndStatus(pmsisdn,userStatus);
	}

	@Override
	public PrimarySubscriptionDom getPrimarySubscriptionByMsisdn(String pmsisdn) {
		return primarySubscriptionRespository.getPrimarySubscriptionByMsisdn(pmsisdn);
	}

	@Override
	public void deletePrimarySubscription(PrimarySubscriptionDom primarySubscription) {
		primarySubscriptionRespository.delete(primarySubscription);		
	}
}