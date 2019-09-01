package com.onmobile.vol.referralchain.app.daemon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.dataaccess.domain.Action;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Status;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;
import com.onmobile.vol.referralchain.app.dto.DtoDomConversionService;
import com.onmobile.vol.referralchain.app.rest.service.SecondarySubscriptionService;

@Component
public class ChildExpiryDaemon {
	
	@Autowired
	SecondarySubscriptionService secondarySubscriptionService;
	
	@Autowired
	DtoDomConversionService dtoDomConverter ;
	
	@Value("${referralchain.child-expiry-daemon.batchSize}")
	private int batchSize;
	
	private int totalBatches ;
		
	private static Logger logger = LogManager.getLogger("childExpiryDaemonLogger");
	
	@Scheduled(cron = "${referralchain.child-expiry-daemon.cron}", zone = "${referralchain.child-expiry-daemon.cron.zone}")
	public void expiredChildSubscriptionAfterExpireDate() {
		
			Date currentDate = new Date() ;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			logger.info("============================== DAEMON STARTED ================================");
			logger.info("Child Expiry Daemon Started Executing Consider CurrentDate : {}" , currentDate);
		
			long totalCount = secondarySubscriptionService.getSecondarySubscriptionCount();
			
			if(totalCount==0) {
				logger.info("No Items in the table to process");
				return ;
			}
			
			if((int) totalCount % batchSize == 0) {
				totalBatches = (int) totalCount / batchSize;
			} else {
				totalBatches = ((int) totalCount / batchSize) + 1;
			}
			logger.info("Daemon Execution will be carried out in "+ totalBatches + " batches");
			
			int lastChildId = 0 ;
			
			for(int batchNo = 1 ; batchNo <= totalBatches ; batchNo ++) {
					logger.info("================= BATCH NO : " + (batchNo) + " =================");
					List<SecondarySubscriptionDom> sSubList = secondarySubscriptionService.getSecondarySubscriptionsByBatch(batchSize, lastChildId);
			
					// empty null check for ssublist
					
					for(SecondarySubscriptionDom ss : sSubList) {
						System.out.println("Element : " + ss.getChildId());
						if(ss.getUserStatus() == UserStatus.PENDING
								&& ss.getStatus() == Status.PENDING
								&& ((sdf.format(currentDate).equals(sdf.format(ss.getExpired()))) || ss.getExpired().before(currentDate))) {
							secondarySubscriptionService.deleteSecondarySubscriptionAndUpdateCountLeft(dtoDomConverter.createSecondarySubscriptionFromDom(ss), Action.CHILD_EXPIRED);
							logger.info("CHILD ID : {} is expired and removed by daemon", ss.getChildId());
					
						} else {
							logger.info("CHILD ID : {} is not an expiry candidate", ss.getChildId());
						}
					
						lastChildId = ss.getChildId() ;
					}
			}
			
			logger.info("============================== DAEMON FINISHED ================================");
	}
}