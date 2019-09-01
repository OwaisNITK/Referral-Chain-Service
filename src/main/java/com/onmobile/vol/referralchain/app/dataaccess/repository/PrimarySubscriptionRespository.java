package com.onmobile.vol.referralchain.app.dataaccess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.onmobile.vol.referralchain.app.dataaccess.domain.PrimarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;

public interface PrimarySubscriptionRespository extends JpaRepository<PrimarySubscriptionDom, Integer> {
	
	@Query("FROM PrimarySubscriptionDom ps WHERE ps.pmsisdn = :pmsisdn")
	public PrimarySubscriptionDom getPrimarySubscriptionByMsisdn(@Param("pmsisdn") String pmsisdn);
	
	@Query("FROM PrimarySubscriptionDom ps WHERE ps.pmsisdn = :pmsisdn AND ps.userStatus = :userStatus")
	public PrimarySubscriptionDom getPrimarySubscriptionByMsisdnAndStatus(
			@Param("pmsisdn") String pmsisdn,
			@Param("userStatus") UserStatus userStatus);
}