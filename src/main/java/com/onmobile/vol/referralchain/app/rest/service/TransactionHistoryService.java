package com.onmobile.vol.referralchain.app.rest.service;


import com.onmobile.vol.referralchain.app.dataaccess.domain.TransactionHistory;
import com.onmobile.vol.referralchain.app.dto.TransactionHistoryRequestDto;
import com.onmobile.vol.referralchain.app.dto.TransactionHistoryResponseDto;

public interface TransactionHistoryService {
	
	public void addTransactionHistory(TransactionHistory tdetails);
		
	public TransactionHistoryResponseDto getTransactionHistoryForMsisdn(TransactionHistoryRequestDto thDto);

}