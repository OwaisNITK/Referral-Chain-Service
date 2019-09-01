package com.onmobile.vol.referralchain.app.rest.service;

import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;
import com.onmobile.vol.referralchain.app.dto.PrimarySubscription;
import com.onmobile.vol.referralchain.app.dto.SecondarySubscription;

public interface PrimarySubscriptionService {
	
	public PrimarySubscription addPrimarySubscription(PrimarySubscription ps);
	
	public PrimarySubscription updatePrimarySubscription(PrimarySubscription ps);

	public PrimarySubscription getPrimarySubscriptionByMsisdnAndStatus(String msisdn,UserStatus userStatus);

	public PrimarySubscription getPrimarySubscriptionByMsisdn(String pmsisdn);

	public void deleteChildUpdateParentAddNewParent(
			SecondarySubscription existingSsub,
			PrimarySubscription newPsub);
}