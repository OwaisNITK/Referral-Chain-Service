package com.onmobile.vol.referralchain.app.junit;

import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.onmobile.vol.referralchain.app.client.ump.SMSNotificationService;
import com.onmobile.vol.referralchain.app.dataaccess.dao.PrimarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.dao.SecondarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.dao.TransactionHistoryDao;
import com.onmobile.vol.referralchain.app.dataaccess.domain.PrimarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.Status;
import com.onmobile.vol.referralchain.app.dataaccess.domain.TransactionHistory;
import com.onmobile.vol.referralchain.app.dataaccess.domain.UserStatus;
import com.onmobile.vol.referralchain.app.dto.DtoDomConversionService;
import com.onmobile.vol.referralchain.app.dto.PrimarySubscription;
import com.onmobile.vol.referralchain.app.dto.SecondarySubscription;
import com.onmobile.vol.referralchain.app.rest.service.impl.SecondarySubscriptionServiceImpl;
import com.onmobile.vol.referralchain.app.utils.CryptoUtil;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SecondarySubscriptionTest {
	
	@Autowired
	SecondarySubscriptionServiceImpl secondarySubscriptionService;
	
	@Mock
	SMSNotificationService smsNotificationService ;
	
	@Autowired
	CryptoUtil cryptoUtil ;
	
	@Autowired
	DtoDomConversionService dtoDomConverter ;
	
	@Mock
	PrimarySubscriptionDao primarySubscriptionDao;
	
	@Mock
	SecondarySubscriptionDao secondarySubscriptionDao ;
	
	@Mock
	TransactionHistoryDao transactionHistoryDao ;
	
	@BeforeEach
	public void setUp() throws Exception {
		// Injecting Mock Dao Layer Instance Into Service Layer
		secondarySubscriptionService.setPrimarySubscriptionDao(primarySubscriptionDao);
		secondarySubscriptionService.setTransactionHistoryDao(transactionHistoryDao);
		secondarySubscriptionService.setSecondarySubscriptionDao(secondarySubscriptionDao);
		secondarySubscriptionService.setSmsNotificationService(smsNotificationService);
		dtoDomConverter.setPrimarySubscriptionDao(primarySubscriptionDao);
	}

	@Test
	public void testAddSecondarySubscriptionService() {
		
		PrimarySubscription existingPsub = new PrimarySubscription() ;
		existingPsub.setParentId(123);
		existingPsub.setPmsisdn("1234567890");
		existingPsub.setUserStatus(UserStatus.ACTIVE);
		existingPsub.setCreated(new Date());
		existingPsub.setLastUpdated(new Date());
		existingPsub.setTotalCount(5);
		existingPsub.setCountLeft(5);
		existingPsub.setMode("APP");
		existingPsub.setOperator("VODAFONE");
		existingPsub.setCircle("INDIA");
		existingPsub.setExternalId("PAID");
		
		PrimarySubscriptionDom existingPsubDom = dtoDomConverter.createPrimarySubscriptionDomFromDto(existingPsub);
		
		SecondarySubscription ss = new SecondarySubscription() ;
		ss.setCmsisdn("0123456789");
		ss.setParentSubscription(existingPsub);
		ss.setParentSubscriptionId(123);
		ss.setPmsisdn(existingPsub.getPmsisdn());
		ss.setpExternalId(existingPsub.getExternalId());
		ss.setUserStatus(UserStatus.PENDING);
		ss.setStatus(Status.PENDING);
		ss.setMode("APP");
		ss.setCreated(new Date());
		ss.setLastUpdated(new Date());
		ss.setProviderName("vodafone");
		ss.setExpired(Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		
		Mockito.when(primarySubscriptionDao.getPrimarySubscriptionByMsisdn(cryptoUtil.encrypt(ss.getPmsisdn()))).thenReturn(existingPsubDom);
		
		SecondarySubscriptionDom ssDom =dtoDomConverter.createSecondarySubscriptionDomFromDto(ss);
		ssDom.setNextRetryTime(null);
		ssDom.setSmsStatus(SMSStatus.SENT);
		ssDom.setSmsRetryCountLeft(0);
		
		PrimarySubscriptionDom updatedPsubDom = new PrimarySubscriptionDom();
		updatedPsubDom.setTotalCount(existingPsubDom.getTotalCount());
		updatedPsubDom.setCountLeft(existingPsubDom.getCountLeft()-1);
		
		Mockito.when(smsNotificationService.sendActivationSMS(ss.getPmsisdn(),ss.getCmsisdn(),ss.getProviderName())).thenReturn(SMSStatus.SENT);
		Mockito.when(secondarySubscriptionDao.addSecondarySubscription(any(SecondarySubscriptionDom.class))).thenReturn(ssDom);
		Mockito.when(primarySubscriptionDao.updatePrimarySubscription(any(PrimarySubscriptionDom.class))).thenReturn(updatedPsubDom);
		Mockito.doNothing().when(transactionHistoryDao).addTransactionHistory(any(TransactionHistory.class));
		
		SecondarySubscription ssDto = secondarySubscriptionService.addSecondarySubscription(ss);
		
		Assertions.assertEquals(ss.getPmsisdn(), ssDto.getPmsisdn());
		Assertions.assertEquals(null, ssDto.getNextRetryTime());
		Assertions.assertEquals(0, ssDto.getSmsRetryCountLeft());
		Assertions.assertEquals(SMSStatus.SENT, ssDto.getSmsStatus());
		Assertions.assertEquals(ss.getUserStatus(), ssDto.getUserStatus());
		Assertions.assertEquals(ss.getMode(), ssDto.getMode());
	}
	
	@Test
	public void testGetSecondarySubscriptionByMsisdnService() {
		System.out.println("Testing Reject Child API");
		
		PrimarySubscription existingPsub = new PrimarySubscription() ;
		existingPsub.setParentId(123);
		existingPsub.setPmsisdn("1234567890");
		existingPsub.setUserStatus(UserStatus.ACTIVE);
		existingPsub.setCreated(new Date());
		existingPsub.setLastUpdated(new Date());
		existingPsub.setTotalCount(5);
		existingPsub.setCountLeft(5);
		existingPsub.setMode("APP");
		existingPsub.setOperator("VODAFONE");
		existingPsub.setCircle("INDIA");
		existingPsub.setExternalId("PAID");

		PrimarySubscriptionDom existingPsubDom = dtoDomConverter.createPrimarySubscriptionDomFromDto(existingPsub);
		
		SecondarySubscription ss = new SecondarySubscription() ;
		ss.setCmsisdn("0123456789");
		ss.setParentSubscription(existingPsub);
		ss.setParentSubscriptionId(123);
		ss.setPmsisdn(existingPsub.getPmsisdn());
		ss.setpExternalId(existingPsub.getExternalId());
		ss.setUserStatus(UserStatus.PENDING);
		ss.setStatus(Status.PENDING);
		ss.setMode("APP");
		ss.setCreated(new Date());
		ss.setLastUpdated(new Date());
		ss.setProviderName("vodafone");
		ss.setExpired(Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		
		Mockito.when(primarySubscriptionDao.getPrimarySubscriptionByMsisdn(cryptoUtil.encrypt(ss.getPmsisdn()))).thenReturn(existingPsubDom);
		
		SecondarySubscriptionDom ssDom =dtoDomConverter.createSecondarySubscriptionDomFromDto(ss);
		
		Mockito.when(secondarySubscriptionDao.getSecondarySubscriptionByMsisdn(cryptoUtil.encrypt(ss.getCmsisdn()))).thenReturn(ssDom);
		
		SecondarySubscription fetchedSsDto = secondarySubscriptionService.getSecondarySubscriptionByMsisdn(ss.getCmsisdn());
		
		Assertions.assertEquals(ss.getCmsisdn(), fetchedSsDto.getCmsisdn());
		Assertions.assertEquals(ss.getPmsisdn(), fetchedSsDto.getPmsisdn());
		Assertions.assertEquals(ss.getParentSubscriptionId(), fetchedSsDto.getParentSubscriptionId());
	}
}