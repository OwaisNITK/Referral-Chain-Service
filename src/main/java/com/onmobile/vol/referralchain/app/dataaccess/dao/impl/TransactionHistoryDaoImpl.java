package com.onmobile.vol.referralchain.app.dataaccess.dao.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.dataaccess.dao.TransactionHistoryDao;
import com.onmobile.vol.referralchain.app.dataaccess.domain.TransactionHistory;
import com.onmobile.vol.referralchain.app.dataaccess.repository.TransactionHistoryRepository;
import com.onmobile.vol.referralchain.app.utils.OffsetBasedPageRequest;

@Component
public class TransactionHistoryDaoImpl implements TransactionHistoryDao {
	
	@Autowired
	TransactionHistoryRepository transactionHistoryRepository ;

	@Override
	public void addTransactionHistory(TransactionHistory tdetails) {
		transactionHistoryRepository.save(tdetails);
	}
		
	@Override
	public Page<TransactionHistory> getAllTransactionsForMsisdnAndStartDateAndEndDate(String msisdn,Date startDate,Date endDate , long offset ,int max ) {
		return transactionHistoryRepository.getAllTransactionsForMsisdnAndStartDateAndEndDate(startDate,endDate,msisdn, OffsetBasedPageRequest.of(offset, max,new Sort(Sort.Direction.DESC, "created")));
	}
}