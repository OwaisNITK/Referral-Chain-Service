package com.onmobile.vol.referralchain.app.daemon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.client.ump.SMSNotificationService;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Action;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;
import com.onmobile.vol.referralchain.app.rest.service.SecondarySubscriptionService;
import com.onmobile.vol.referralchain.app.utils.CryptoUtil;

@Component
public class SMSNotificationDaemon {


	private static Logger logger = LogManager.getLogger("smsDaemonLogger");
	
	@Autowired
	SecondarySubscriptionService secondarySubscriptionService;
	
	@Autowired
	private SMSNotificationService smsNotificationService;

	@Autowired
	private CryptoUtil cryptoUtil ;
	
	@Resource(name = "smsNotificationSenderDaemonTaskExecutor")
	private TaskExecutor smsNotificationSenderDaemonTaskExecutor;

	@Value("${referralchain.sms-notification.next.retry.time.in.minutes}")
	private int nextRetryTimeInMinutes;
	
	@Scheduled(fixedDelayString = "${referralchain.sms-notification.daemon.fixedDelay}", initialDelayString= "${referralchain.sms-notification.daemon.initialDelay}")
	public void sendSMSNotificationToPendingChilds() {

		List<SecondarySubscriptionDom> smsCandidates = secondarySubscriptionService.getAllSMSNotificationCandidates(SMSStatus.PENDING, new Date());

		if (smsCandidates == null || smsCandidates.isEmpty()) {
			logger.debug("No SMS Candidates Found By SMS Daemon");
			return;
		}

		List<Future<Boolean>> taskExecutorFutures = new ArrayList<>();

		for (SecondarySubscriptionDom ss : smsCandidates) {
			taskExecutorFutures.add(sendSMS(ss));
		}

		taskExecutorFutures.stream().forEach(future -> {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error("SMS Daemon Interrupted");
			}
		});

	}

	private Future<Boolean> sendSMS(SecondarySubscriptionDom ss) {

		return smsNotificationSenderDaemonTaskExecutor.submit(() -> {

			String plainTextPmsisdn = cryptoUtil.decrypt(ss.getParentSubscriptionDom().getPmsisdn());
			String plainTextCmsisdn = cryptoUtil.decrypt(ss.getCmsisdn());
			
			try {

				SMSStatus smsStatus = smsNotificationService.sendActivationSMS(plainTextPmsisdn,plainTextCmsisdn,ss.getProviderName());
				if (smsStatus == SMSStatus.SENT) {
					ss.setSmsStatus(smsStatus);
					ss.setNextRetryTime(null);
					ss.setSmsRetryCountLeft(0);
					secondarySubscriptionService.updateSecondarySubscriptionForSMSStatus(ss, smsStatus,Action.SMS_STATUS_CHANGE);
					logger.info("Message successfuly sent to Child Msisdn : {}" ,plainTextCmsisdn);
				} else {
					int smsRetryCountLeft =  ss.getSmsRetryCountLeft() - 1;
					ss.setSmsRetryCountLeft(smsRetryCountLeft);
					ss.setNextRetryTime(secondarySubscriptionService.resolveNextRetryTime(nextRetryTimeInMinutes));
					secondarySubscriptionService.updateSecondarySubscriptionForSMSStatus(ss, smsStatus,Action.NO_ACTION);
					logger.info("Message failed to send for Child Msisdn : {} and remaining  retry count is : {}" ,plainTextCmsisdn,smsRetryCountLeft);
				}
				return true;
			} catch (Exception e) {
				logger.error("Exception occured while sending sms to CMSISDN : {} ",plainTextCmsisdn);
				return false;
			}
		});
	}
}