package com.onmobile.vol.referralchain.app.dataaccess.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.onmobile.vol.referralchain.app.dataaccess.domain.TransactionHistory;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {
	
	@Query(value = "FROM TransactionHistory th where th.created BETWEEN :startDate AND :endDate AND  th.pmsisdn = :pmsisdn")
	public Page<TransactionHistory> getAllTransactionsForMsisdnAndStartDateAndEndDate(@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("pmsisdn") String pmsisdn, Pageable pageRequest);
}