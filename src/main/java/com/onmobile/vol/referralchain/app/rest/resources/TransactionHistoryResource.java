package com.onmobile.vol.referralchain.app.rest.resources;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onmobile.vol.referralchain.app.dto.TransactionHistoryRequestDto;
import com.onmobile.vol.referralchain.app.dto.TransactionHistoryResponseDto;
import com.onmobile.vol.referralchain.app.rest.service.TransactionHistoryService;

@RestController
@RequestMapping("/transaction")
public class TransactionHistoryResource {
	
	public static Logger logger  = LogManager.getLogger(TransactionHistoryResource.class);
	@Autowired
	TransactionHistoryService transactionHistoryService;
	
	@GetMapping("/{msisdn}")
	public TransactionHistoryResponseDto getTransactionHistoryForMsisdn(
			@PathVariable String msisdn,
			@RequestParam(value="mode",required=true) String mode ,
			@RequestParam(value="startDate",required=false)@DateTimeFormat(pattern="yyyyMMdd") Date startDate,
			@RequestParam(value="endDate",required=false)@DateTimeFormat(pattern="yyyyMMdd") Date endDate ,
			@RequestParam(value="offset",required=false, defaultValue="0") int offset ,
			@RequestParam(value="max",required=false, defaultValue="10") int max) {
		logger.debug("getTransactionHistoryForMsisdn API starts for msisdn : {} ",msisdn);
		TransactionHistoryRequestDto thDto = new TransactionHistoryRequestDto();
		thDto.setMsisdn(msisdn);
		thDto.setMode(mode);
		thDto.setStartDate(startDate);
		thDto.setEndDate(endDate);
		thDto.setOffset(offset);
		thDto.setMax(max);
		
		return transactionHistoryService.getTransactionHistoryForMsisdn(thDto) ;
	}
}