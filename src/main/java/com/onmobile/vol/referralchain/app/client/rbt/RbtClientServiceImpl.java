package com.onmobile.vol.referralchain.app.client.rbt;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.client.HttpClientFactory;
import com.onmobile.vol.referralchain.app.config.ProviderDetailsResolver;
import com.onmobile.vol.referralchain.app.errors.ErrorCode;
import com.onmobile.vol.referralchain.app.exceptions.ReferralChainServiceException;

@Component
public class RbtClientServiceImpl implements RbtClientService {
	
	public static Logger logger  = LogManager.getLogger(RbtClientServiceImpl.class);
	
	public static final String GET_RBT_SUBSCRIPTION_API_PATH = "/subscription/voltron";
	public static final String DEACTIVATE_RBT_SUBSCRIPTION_API_PATH = "/subscription/update/voltron";

	@Autowired
	private HttpClientFactory httpClientFactory ;
	
	@Autowired
	private ProviderDetailsResolver providerDetailsResolver ;
	
	@Value("${referralchain.dummy.catalog.subscription.id}")
	private String dummyCatalogSubscriptionId;
	
	@Override
	public RbtSubscriptionResponseDto getRbtSubscriptionInfo(String msisdn, String providerName) {
		
		RbtSubscriptionResponseDto response = new RbtSubscriptionResponseDto() ;
		
		String rbtBaseUrl = providerDetailsResolver.resolveRbtUrlForProvider(providerName);
		URI uri = prepareGetRbtSubscriptionURI(rbtBaseUrl,msisdn);
		logger.debug("GetRbtSubscriptionURI : {}  for msisdn : {} ",uri.toString(),msisdn);
		HttpGet request = new HttpGet(uri);
		CloseableHttpClient client = httpClientFactory.createHttpClient();
			
		try {
				response = client.execute(request, new RbtSubscriptionResponseHandler<RbtSubscriptionResponseDto>());
		} catch (ClientProtocolException e) {
			logger.error("while hitting GetRbtSubscription error occurred for msisdn : {} and the message is : {}  ",msisdn,e.getMessage());
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.RBT_ERROR,
					e.getMessage());  
		} catch (IOException e) {
			logger.error("while hitting GetRbtSubscription error occurred for msisdn : {} and the message is : {}  ",msisdn,e.getMessage());
			throw new ReferralChainServiceException(
					ErrorCode.REFERRAL_SERVICE_ERROR,
					ErrorCode.RBT_ERROR,
					e.getMessage());  
		} 
		
		return response ;
	}

	private URI prepareGetRbtSubscriptionURI(String rbtBaseUrl, String msisdn) {
		
		String getSubscriptionUrl = rbtBaseUrl.concat(GET_RBT_SUBSCRIPTION_API_PATH);
		URI uri = null ;
		
		try {
			uri = new URIBuilder()
						.setPath(getSubscriptionUrl)
						.setParameter("subscriberId", msisdn)
						.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		return uri ;
	}

	@Override
	public String DeactivateRbtSubscription(String msisdn, String providerName, String mode) {

		String rbtDeactivateSubscriptionStatus ;
		
		String rbtBaseUrl = providerDetailsResolver.resolveRbtUrlForProvider(providerName);
		String status = "CANCELED" ;
		URI uri = prepareDeactivateRbtSubscriptionURI(rbtBaseUrl,msisdn,status,mode,dummyCatalogSubscriptionId);
		logger.debug("DeactivateRbtSubscriptionURI: {} for msisdn : {} ",uri,msisdn);
		HttpPost request = new HttpPost(uri);
		CloseableHttpClient client = httpClientFactory.createHttpClient();
		
		try {
			rbtDeactivateSubscriptionStatus = client.execute(request, new RBTDeactivationResponseHandler<String>());
	} catch (ClientProtocolException e) {
		logger.error("while hitting DeactivateRbtSubscription error occurred for msisdn : {} and the message is : {}  ",msisdn,e.getMessage());
		throw new ReferralChainServiceException(
				ErrorCode.REFERRAL_SERVICE_ERROR,
				ErrorCode.RBT_ERROR,
				e.getMessage());  
	} catch (IOException e) {
		logger.error("while hitting DeactivateRbtSubscription error occurred for msisdn : {} and the message is : {}  ",msisdn,e.getMessage());
		throw new ReferralChainServiceException(
				ErrorCode.REFERRAL_SERVICE_ERROR,
				ErrorCode.RBT_ERROR,
				e.getMessage());  
	} 
		return rbtDeactivateSubscriptionStatus;
	}
	
	private URI prepareDeactivateRbtSubscriptionURI(String rbtBaseUrl, String msisdn, String status, String mode , String catalogSubscriptionId) {
		
		String deactivateRbtSubscriptionUrl = rbtBaseUrl.concat(DEACTIVATE_RBT_SUBSCRIPTION_API_PATH);
		URI uri = null ;
		
		try {
			uri = new URIBuilder()
						.setPath(deactivateRbtSubscriptionUrl)
						.setParameter("subscriberId", msisdn)
						.setParameter("status", status)
						.setParameter("mode", mode)
						.setParameter("catalogSubscriptionId", catalogSubscriptionId)
						.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		return uri ;
	}	
}