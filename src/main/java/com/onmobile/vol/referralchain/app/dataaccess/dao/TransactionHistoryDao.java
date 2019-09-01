package com.onmobile.vol.referralchain.app.dataaccess.dao;

import java.util.Date;

import org.springframework.data.domain.Page;

import com.onmobile.vol.referralchain.app.dataaccess.domain.TransactionHistory;

public interface TransactionHistoryDao {
	
	public void addTransactionHistory(TransactionHistory tdetails);

	public Page<TransactionHistory> getAllTransactionsForMsisdnAndStartDateAndEndDate(String msisdn,Date startDate,Date interval,long offset,int max ) ;
	
}