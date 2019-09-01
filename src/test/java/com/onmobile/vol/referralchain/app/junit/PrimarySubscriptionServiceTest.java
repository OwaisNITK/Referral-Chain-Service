package com.onmobile.vol.referralchain.app.junit;


import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.onmobile.vol.referralchain.app.rest.service.impl.PrimarySubscriptionServiceImpl;
import com.onmobile.vol.referralchain.app.utils.CryptoUtil;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PrimarySubscriptionServiceTest {
	
	@Autowired
	PrimarySubscriptionServiceImpl primarySubscriptionService ;	
	
	@Autowired
	CryptoUtil cryptoUtil ;
	
	@Autowired
	DtoDomConversionService domService  ;
	
	@Mock
	PrimarySubscriptionDao primarySubscriptionDao ;
	
	@Mock
	TransactionHistoryDao transactionHistoryDao ;
	
	@Mock
	SecondarySubscriptionDao secondarySubscriptionDao ;
		
	@BeforeEach
	public void setUp() throws Exception {
		// Injecting Mock Dao Layer Instance Into Service Layer
		primarySubscriptionService.setPrimarySubscriptionDao(primarySubscriptionDao);
		primarySubscriptionService.setTransactionHistoryDao(transactionHistoryDao);
		primarySubscriptionService.setSecondarySubscriptionDao(secondarySubscriptionDao);
	}
	
	//@MockitoSettings(strictness = Strictness.WARN)
	@Test
	public void testAddPrimarySubscriptionService() {
		System.out.println("Testing Add Primary Subscription");
		
		PrimarySubscription newPsub = new PrimarySubscription() ;
		newPsub.setPmsisdn("1234567890");
		newPsub.setUserStatus(UserStatus.PENDING);
		newPsub.setCreated(new Date());
		newPsub.setLastUpdated(new Date());
		newPsub.setTotalCount(-1);
		newPsub.setCountLeft(-1);
		newPsub.setMode("APP");
		newPsub.setOperator("VODAFONE");
		newPsub.setCircle("INDIA");
		newPsub.setExternalId("PAID");
		
		PrimarySubscriptionDom newPsDom = domService.createPrimarySubscriptionDomFromDto(newPsub);

		Mockito.doNothing().when(transactionHistoryDao).addTransactionHistory(any(TransactionHistory.class));
		Mockito.when(primarySubscriptionDao.addPrimarySubscription(any(PrimarySubscriptionDom.class))).thenReturn(newPsDom);
		
		PrimarySubscription psDto = primarySubscriptionService.addPrimarySubscription(newPsub);
		
		Assertions.assertEquals(newPsDom.getPmsisdn(), cryptoUtil.encrypt(psDto.getPmsisdn()));
		Assertions.assertEquals(newPsDom.getUserStatus(), psDto.getUserStatus());
		Assertions.assertEquals(newPsDom.getTotalCount(), psDto.getTotalCount());
		Assertions.assertEquals(newPsDom.getCountLeft(), psDto.getCountLeft());
		Assertions.assertEquals(newPsDom.getMode(), psDto.getMode());
		Assertions.assertEquals(newPsDom.getOperator(), psDto.getOperator());
		Assertions.assertEquals(newPsDom.getExternalId(), psDto.getExternalId());
	}
	
	@Test
	public void testGetPrimarySubscriptionByMsisdnService() throws Exception {
	
		String plainMsisdn = "1234567890" ;
		String encryptedMsisdn = cryptoUtil.encrypt(plainMsisdn);
		
		PrimarySubscriptionDom psDom = new PrimarySubscriptionDom();
		psDom.setParentId(123);
		psDom.setPmsisdn(encryptedMsisdn);
		psDom.setUserStatus(UserStatus.PENDING);
		psDom.setTotalCount(-1);
		psDom.setCountLeft(-1);
		psDom.setExternalId("PAID");
		psDom.setOperator("VODAFONE");
		psDom.setCircle("IND");
		psDom.setMode("APP");
			
		Date currentTimeStamp = new Date() ;
		psDom.setCreated(currentTimeStamp);
		psDom.setLastUpdated(currentTimeStamp);
		
		Mockito.when(primarySubscriptionDao.getPrimarySubscriptionByMsisdn(encryptedMsisdn)).thenReturn(psDom);
		
		PrimarySubscription psDto = primarySubscriptionService.getPrimarySubscriptionByMsisdn(plainMsisdn);
		
		Assertions.assertEquals(psDom.getPmsisdn(),cryptoUtil.encrypt(psDto.getPmsisdn()));
		Assertions.assertEquals(psDom.getUserStatus(),psDto.getUserStatus());
		Assertions.assertEquals(psDom.getTotalCount(),psDto.getTotalCount());
		Assertions.assertEquals(psDom.getCountLeft(),psDto.getCountLeft());
		Assertions.assertEquals(psDom.getExternalId(),psDto.getExternalId());
		Assertions.assertEquals(psDom.getOperator(),psDto.getOperator());
		Assertions.assertEquals(psDom.getCircle(),psDto.getCircle());
		Assertions.assertEquals(psDom.getChilds(),psDto.getChilds());
	}
	
	@Test
	public void testUpdateSubscriptionService() {
		
		PrimarySubscription existingPsub = new PrimarySubscription();
		existingPsub.setPmsisdn("1234567890");
        existingPsub.setUserStatus(UserStatus.PENDING);
        existingPsub.setCreated(new Date());
        existingPsub.setLastUpdated(new Date());
        existingPsub.setTotalCount(-1);
        existingPsub.setCountLeft(-1);
        existingPsub.setMode("APP");
        existingPsub.setOperator("VODAFONE");
        existingPsub.setCircle("INDIA");
        existingPsub.setExternalId("PAID");
        
        PrimarySubscriptionDom updatedPsDom = domService.createPrimarySubscriptionDomFromDto(existingPsub);
        
        Mockito.when(primarySubscriptionDao.updatePrimarySubscription(any(PrimarySubscriptionDom.class))).thenReturn(updatedPsDom);
		
		PrimarySubscription updatedPsDto = primarySubscriptionService.updatePrimarySubscription(existingPsub);
		
		Assertions.assertEquals(existingPsub.getPmsisdn(), updatedPsDto.getPmsisdn());
		Assertions.assertEquals(existingPsub.getUserStatus(), updatedPsDto.getUserStatus());
		Assertions.assertEquals(existingPsub.getTotalCount(), updatedPsDto.getTotalCount());
		Assertions.assertEquals(existingPsub.getCountLeft(), updatedPsDto.getCountLeft());
		Assertions.assertEquals(existingPsub.getMode(), updatedPsDto.getMode());
		Assertions.assertEquals(existingPsub.getOperator(), updatedPsDto.getOperator());
		Assertions.assertEquals(existingPsub.getCircle(), updatedPsDto.getCircle());
		Assertions.assertEquals(existingPsub.getExternalId(), updatedPsDto.getExternalId());
        
	}

	@Test
	public void testGetPrimarySubscriptionByMsisdnAndStatusService() {
		
		String plainMsisdn = "1234567890" ;
		String encryptedMsisdn = cryptoUtil.encrypt(plainMsisdn);
		
		PrimarySubscriptionDom psDom = new PrimarySubscriptionDom();
		psDom.setParentId(123);
		psDom.setPmsisdn(encryptedMsisdn);
		psDom.setUserStatus(UserStatus.ACTIVE);
		psDom.setTotalCount(5);
		psDom.setCountLeft(3);
		psDom.setExternalId("PAID");
		psDom.setOperator("VODAFONE");
		psDom.setCircle("IND");
		psDom.setMode("APP");
			
		Date currentTimeStamp = new Date() ;
		psDom.setCreated(currentTimeStamp);
		psDom.setLastUpdated(currentTimeStamp);
		
		Mockito.when(primarySubscriptionDao.getPrimarySubscriptionByMsisdnAndStatus(encryptedMsisdn, UserStatus.ACTIVE)).thenReturn(psDom);
		
		PrimarySubscription psDto = primarySubscriptionService.getPrimarySubscriptionByMsisdnAndStatus(plainMsisdn, UserStatus.ACTIVE);
		
		Assertions.assertEquals(psDom.getPmsisdn(),cryptoUtil.encrypt(psDto.getPmsisdn()));
		Assertions.assertEquals(psDom.getUserStatus(),psDto.getUserStatus());
		Assertions.assertEquals(psDom.getTotalCount(),psDto.getTotalCount());
		Assertions.assertEquals(psDom.getCountLeft(),psDto.getCountLeft());
		Assertions.assertEquals(psDom.getExternalId(),psDto.getExternalId());
		Assertions.assertEquals(psDom.getOperator(),psDto.getOperator());
		Assertions.assertEquals(psDom.getCircle(),psDto.getCircle());
		Assertions.assertEquals(psDom.getChilds(),psDto.getChilds());	
	}
	
	@Test
	public void testDeleteChildUpdateParentAddNewParentService() {
		
		PrimarySubscription existingSsubParent = new PrimarySubscription() ;
		existingSsubParent.setParentId(123);
		existingSsubParent.setPmsisdn("1234567890");
		existingSsubParent.setUserStatus(UserStatus.PENDING);
		existingSsubParent.setCreated(new Date());
		existingSsubParent.setLastUpdated(new Date());
		existingSsubParent.setTotalCount(-1);
		existingSsubParent.setCountLeft(-1);
		existingSsubParent.setMode("APP");
		existingSsubParent.setOperator("VODAFONE");
		existingSsubParent.setCircle("INDIA");
		existingSsubParent.setExternalId("PAID");
		
		PrimarySubscriptionDom existingPsubParentDom = domService.createPrimarySubscriptionDomFromDto(existingSsubParent);
		
		SecondarySubscription existingSsub = new SecondarySubscription();
		existingSsub.setChildId(456);
		existingSsub.setParentSubscriptionId(123);
		existingSsub.setParentSubscription(existingSsubParent);
		existingSsub.setCmsisdn("0987654321");
		existingSsub.setPmsisdn("1234567890");
		existingSsub.setCreated(new Date());
		existingSsub.setExpired(Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		existingSsub.setMode("APP");
		existingSsub.setNextRetryTime(null);
		existingSsub.setExternalId("FREE");
		existingSsub.setpExternalId("PAID");	
		existingSsub.setProviderName("vodafone");
		existingSsub.setSmsRetryCountLeft(0);
		existingSsub.setSmsStatus(SMSStatus.SENT);
		existingSsub.setStatus(Status.PENDING);
		existingSsub.setUserStatus(UserStatus.PENDING);
		
		List<SecondarySubscription> childs = new ArrayList<SecondarySubscription>();
		childs.add(existingSsub);
		existingSsubParent.setChilds(childs);
		
		SecondarySubscriptionDom existingSsubDom = domService.createSecondarySubscriptionDomFromDto(existingSsub);
		existingSsubDom.setParentSubscriptionDom(existingPsubParentDom);
		
		PrimarySubscription newPsub = new PrimarySubscription() ;
		newPsub.setPmsisdn("0987654321");
		newPsub.setUserStatus(UserStatus.PENDING);
		newPsub.setCreated(new Date());
		newPsub.setLastUpdated(new Date());
		newPsub.setTotalCount(-1);
		newPsub.setCountLeft(-1);
		newPsub.setMode("APP");
		newPsub.setOperator("VODAFONE");
		newPsub.setCircle("INDIA");
		newPsub.setExternalId("PAID");
		
		PrimarySubscriptionDom newPsDom = domService.createPrimarySubscriptionDomFromDto(newPsub);
		
		Mockito.when(secondarySubscriptionDao.getSecondarySubscriptionByMsisdn(cryptoUtil.encrypt(existingSsub.getCmsisdn())))
				.thenReturn(existingSsubDom);
		Mockito.doNothing().when(secondarySubscriptionDao).deleteSecondarySubscriptionById(existingSsub.getChildId());
		Mockito.when(primarySubscriptionDao.addPrimarySubscription(any(PrimarySubscriptionDom.class))).thenReturn(newPsDom);
		Mockito.doNothing().when(transactionHistoryDao).addTransactionHistory(any(TransactionHistory.class));
		
		primarySubscriptionService.deleteChildUpdateParentAddNewParent(existingSsub,newPsub);
		
	}
}