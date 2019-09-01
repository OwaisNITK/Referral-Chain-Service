package com.onmobile.vol.referralchain.app.dataaccess.dao;

import com.onmobile.vol.referralchain.app.dataaccess.domain.PrimarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;

public interface PrimarySubscriptionDao {
	
	public PrimarySubscriptionDom addPrimarySubscription(PrimarySubscriptionDom psubscription) ;
	
	public PrimarySubscriptionDom updatePrimarySubscription(PrimarySubscriptionDom ps);
	
	public PrimarySubscriptionDom getPrimarySubscriptionByMsisdnAndStatus(String pmsisdn, UserStatus userStatus) ;

	public PrimarySubscriptionDom getPrimarySubscriptionByMsisdn(String pmsisdn);

	public void deletePrimarySubscription(PrimarySubscriptionDom primarySubscription);
}