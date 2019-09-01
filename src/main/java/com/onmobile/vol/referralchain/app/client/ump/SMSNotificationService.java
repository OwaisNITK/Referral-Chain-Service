package com.onmobile.vol.referralchain.app.client.ump;

import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;

public interface SMSNotificationService {

	public SMSStatus sendActivationSMS(String pmsisdn, String cmsisdn,String providerName);
}
