package com.onmobile.vol.referralchain.app.dataaccess.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;

public interface SecondarySubscriptionRepository extends JpaRepository<SecondarySubscriptionDom, Integer> {

	@Query("FROM SecondarySubscriptionDom ss WHERE ss.cmsisdn = :cmsisdn")
	SecondarySubscriptionDom getSecondarySubscriptionByMsisdn(@Param("cmsisdn") String cmsisdn);

	@Query("FROM SecondarySubscriptionDom ss WHERE ss.smsStatus = :smsStatus AND smsRetryCountLeft > 0 AND nextRetryTime < :currentDate")
	List<SecondarySubscriptionDom> getAllSMSNotificationCandidates(@Param("smsStatus") SMSStatus smsStatus, @Param("currentDate") Date currentDate);
	
	@Query(value="FROM SecondarySubscriptionDom WHERE childId > :lastChildId")
	List<SecondarySubscriptionDom> getSecondarySubscriptionsByBatch(@Param("lastChildId")int lastChildId,Pageable pageRequest);
}