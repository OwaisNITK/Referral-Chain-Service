package com.onmobile.vol.referralchain.app.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.dataaccess.dao.PrimarySubscriptionDao;
import com.onmobile.vol.referralchain.app.dataaccess.domain.PrimarySubscriptionDom;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SecondarySubscriptionDom;
import com.onmobile.vol.referralchain.app.utils.CryptoUtil;

@Component
public class DtoDomConversionService {
	
	@Autowired
	CryptoUtil cryptoUtil ;
	
	@Autowired
	PrimarySubscriptionDao primarySubscriptionDao;
	
	public PrimarySubscription createPrimarySubscriptionFromDom(PrimarySubscriptionDom pDom) {

		PrimarySubscription ps = new PrimarySubscription();
		
		ps.setParentId(pDom.getParentId());
		ps.setPmsisdn(cryptoUtil.decrypt(pDom.getPmsisdn()));
		ps.setUserStatus(pDom.getUserStatus());
		ps.setCreated(pDom.getCreated());
		ps.setLastUpdated(pDom.getLastUpdated());
		ps.setTotalCount(pDom.getTotalCount());
		ps.setCountLeft(pDom.getCountLeft());
		ps.setExternalId(pDom.getExternalId());
		ps.setCircle(pDom.getCircle());
		ps.setOperator(pDom.getOperator());
		ps.setType(pDom.getType());
		ps.setMode(pDom.getMode());
		
		List<SecondarySubscription> childs = new ArrayList<SecondarySubscription>();
		
		if(pDom.getChilds() != null) {
			if(!pDom.getChilds().isEmpty()) {
				for (SecondarySubscriptionDom ssDom : pDom.getChilds()) {
					childs.add(createSecondarySubscriptionFromDom(ssDom));
				}
			}
			ps.setChilds(childs);
		}
		
		return ps ;
	}
	
	public PrimarySubscriptionDom createPrimarySubscriptionDomFromDto(PrimarySubscription ps) {

		PrimarySubscriptionDom psDom = new PrimarySubscriptionDom();
		
		psDom.setParentId(ps.getParentId());
		psDom.setPmsisdn(cryptoUtil.encrypt(ps.getPmsisdn()));
		psDom.setUserStatus(ps.getUserStatus());
		psDom.setCreated(ps.getCreated());
		psDom.setLastUpdated(ps.getLastUpdated());
		psDom.setTotalCount(ps.getTotalCount());
		psDom.setCountLeft(ps.getCountLeft());
		psDom.setExternalId(ps.getExternalId());
		psDom.setCircle(ps.getCircle());
		psDom.setOperator(ps.getOperator());
		psDom.setType(ps.getType());
		psDom.setMode(ps.getMode());
		
		List<SecondarySubscriptionDom> childs = new ArrayList<SecondarySubscriptionDom>();
		
		if(ps.getChilds()!=null) {
			if(!ps.getChilds().isEmpty()) {
				for (SecondarySubscription ss : ps.getChilds()) {
					childs.add(createSecondarySubscriptionDomFromDto(ss));
				}
			}
			psDom.setChilds(childs);
		}
		
		return psDom ;
	}
	
	public SecondarySubscription createSecondarySubscriptionFromDom(SecondarySubscriptionDom ssDom) {
		SecondarySubscription ss = new SecondarySubscription();
		
		ss.setChildId(ssDom.getChildId());
		ss.setCmsisdn(cryptoUtil.decrypt(ssDom.getCmsisdn()));
		ss.setUserStatus(ssDom.getUserStatus());
		ss.setStatus(ssDom.getStatus());
		ss.setCreated(ssDom.getCreated());
		ss.setLastUpdated(ssDom.getLastUpdated());
		ss.setExpired(ssDom.getExpired());
		ss.setType(ssDom.getType());
		ss.setMode(ssDom.getMode());
		ss.setExternalId(ssDom.getExternalId());
		ss.setProviderName(ssDom.getProviderName());
		ss.setSmsStatus(ssDom.getSmsStatus());
		ss.setSmsRetryCountLeft(ssDom.getSmsRetryCountLeft());
		ss.setNextRetryTime(ssDom.getNextRetryTime());
		ss.setParentSubscriptionId(ssDom.getParentSubscriptionDom().getParentId());
		ss.setPmsisdn(cryptoUtil.decrypt(ssDom.getParentSubscriptionDom().getPmsisdn()));
		ss.setpExternalId(ssDom.getParentSubscriptionDom().getExternalId());
		//ss.setParentSubscription(createPrimarySubscriptionFromDom(ssDom.getParentSubscription()));
		
		return ss ;
	}
	
	public SecondarySubscriptionDom createSecondarySubscriptionDomFromDto(SecondarySubscription ss) {
		
		SecondarySubscriptionDom ssDom = new SecondarySubscriptionDom();
		
		if(ss.getChildId()!=0) {
			ssDom.setChildId(ss.getChildId());
		}
		
		ssDom.setCmsisdn(cryptoUtil.encrypt(ss.getCmsisdn()));
		ssDom.setUserStatus(ss.getUserStatus());
		ssDom.setStatus(ss.getStatus());
		ssDom.setCreated(ss.getCreated());
		ssDom.setLastUpdated(ss.getLastUpdated());
		ssDom.setExpired(ss.getExpired());
		ssDom.setType(ss.getType());
		ssDom.setMode(ss.getMode());
		ssDom.setExternalId(ss.getExternalId());
		ssDom.setProviderName(ss.getProviderName());
		ssDom.setSmsStatus(ss.getSmsStatus());
		ssDom.setSmsRetryCountLeft(ss.getSmsRetryCountLeft());
		ssDom.setNextRetryTime(ss.getNextRetryTime());
		ssDom.setParentSubscriptionDom(primarySubscriptionDao.getPrimarySubscriptionByMsisdn(cryptoUtil.encrypt(ss.getPmsisdn())));
		
		return ssDom;
	}

	//Setters Define To Inject Mock Dao Layer Instance for JUnit
	public void setPrimarySubscriptionDao(PrimarySubscriptionDao primarySubscriptionDao) {
		this.primarySubscriptionDao = primarySubscriptionDao;
	}
}