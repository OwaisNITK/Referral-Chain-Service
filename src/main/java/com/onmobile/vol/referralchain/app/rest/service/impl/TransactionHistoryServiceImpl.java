package com.onmobile.vol.referralchain.app.rest.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.dataaccess.dao.TransactionHistoryDao;
import com.onmobile.vol.referralchain.app.dataaccess.domain.TransactionHistory;
import com.onmobile.vol.referralchain.app.dto.TransactionBaseDto;
import com.onmobile.vol.referralchain.app.dto.TransactionHistoryRequestDto;
import com.onmobile.vol.referralchain.app.dto.TransactionHistoryResponseDto;
import com.onmobile.vol.referralchain.app.errors.ErrorCode;
import com.onmobile.vol.referralchain.app.exceptions.ReferralChainServiceException;
import com.onmobile.vol.referralchain.app.rest.service.TransactionHistoryService;
import com.onmobile.vol.referralchain.app.utils.CryptoUtil;

@Component
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss Z";
	
	public static Logger logger  = LogManager.getLogger(TransactionHistoryServiceImpl.class);
	
	@Autowired
	TransactionHistoryDao transactionHistoryDao ;
	
	@Autowired
	CryptoUtil cryptoUtil ;
		
	@Override
	public TransactionHistoryResponseDto getTransactionHistoryForMsisdn(TransactionHistoryRequestDto thDto) {
		
		Date startDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
		Calendar cal = Calendar.getInstance();
		Date endDate = null;
				
		if(thDto.getStartDate() != null) {
			
			if(thDto.getEndDate() == null) {
				endDate = convertDateFormat(cal,thDto.getStartDate(),31,sdf);
			} else {
				endDate = convertDateFormat(cal,thDto.getEndDate(),1,sdf);
			}
			
			startDate = convertDateFormat(cal,thDto.getStartDate(),0,sdf);
		
		} else {
			endDate = convertDateFormat(cal,cal.getTime(),1,sdf);
			startDate  = convertDateFormat(cal,cal.getTime(),-31,sdf);			
		}
		
		Page<TransactionHistory> transactionHistoryDBList = null;
		List<TransactionBaseDto> finalListFromDB= new ArrayList<>();
		logger.debug("feteching Transactions for msisdn : {} and pararmeters are :  startDate is : {}  endDate is : {}  offset is : {}  max is : {}", thDto.getMsisdn().toString() ,startDate,endDate,thDto.getOffset(),thDto.getMax());
		String encryptedMsisdn = cryptoUtil.encrypt(thDto.getMsisdn());
		transactionHistoryDBList =transactionHistoryDao.getAllTransactionsForMsisdnAndStartDateAndEndDate(encryptedMsisdn,startDate,endDate,thDto.getOffset(),thDto.getMax());				
		for(TransactionHistory  t1 : transactionHistoryDBList) {
			TransactionBaseDto transaction = new TransactionBaseDto();
			transaction.setAction(t1.getAction());
			
			if(t1.getCmsisdn()!=null) {
				transaction.setChildMsisdn(cryptoUtil.decrypt(t1.getCmsisdn()));
			}
			
			transaction.setMessage(t1.getInfo());
			transaction.setDate(modifyDateFormate(DATE_TIME_PATTERN,t1.getCreated()));
			finalListFromDB.add(transaction);
		}		
		
		TransactionHistoryResponseDto transactionHistoryResponse  = new TransactionHistoryResponseDto();
		transactionHistoryResponse.setTransactions(finalListFromDB);
		transactionHistoryResponse.setOffset(thDto.getOffset());
		transactionHistoryResponse.setTotalCount(transactionHistoryDBList.getTotalElements());;
		transactionHistoryResponse.setItemCount(finalListFromDB.size());
		logger.debug("TransactionHistoryResponse  for msisdn : {}  is  : {} ",thDto.getMsisdn(),transactionHistoryResponse);
		return transactionHistoryResponse;
	}

	@Override
	public void addTransactionHistory(TransactionHistory tdetails) {
		transactionHistoryDao.addTransactionHistory(tdetails);
	}
	
	public Date convertDateFormat(Calendar cal , Date date, int numberOfDays, SimpleDateFormat sdf) {
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, numberOfDays);  
		String endDateInString = sdf.format(cal.getTime());
		Date dateReturn = null;
		try {
			dateReturn = sdf.parse(endDateInString);
		} catch (ParseException e) {
			throw new ReferralChainServiceException(
                    ErrorCode.REFERRAL_SERVICE_ERROR,
                    ErrorCode.PARSING_ERROR,
                    " Error while parsing date");
		}
		return dateReturn;
	}
	
	public String modifyDateFormate(String pattern, Date date) {
		DateFormat dateFormat = new SimpleDateFormat(pattern);  
        String strDate = dateFormat.format(date);  
		
		return strDate;
	}
}