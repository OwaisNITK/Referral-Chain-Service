package com.onmobile.vol.referralchain.app.client.rbt;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onmobile.vol.referralchain.app.errors.ErrorCode;
import com.onmobile.vol.referralchain.app.exceptions.ReferralChainServiceException;

public class RBTDeactivationResponseHandler<T> implements ResponseHandler<String> {

	public static Logger logger  = LogManager.getLogger(RBTDeactivationResponseHandler.class); 
	
	@Override
	public String handleResponse(HttpResponse rbtResponse) throws ClientProtocolException, IOException {
		
		int statusCode = rbtResponse.getStatusLine().getStatusCode();
		
		String rbtDeactivateSubscriptionStatus = null; ;
		logger.debug("Rbt resposne status is  : {} ",statusCode);
		 if (statusCode==200) {
			// Parse JSON Data and Prepare RbtDeactivateSubscriptionStatus
			HttpEntity responseEntity = rbtResponse.getEntity();
			byte[] jsonData = EntityUtils.toByteArray(responseEntity);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(jsonData);
			JsonNode status = rootNode.path("status");
			rbtDeactivateSubscriptionStatus = status.asText();
			 logger.debug("Rbt Success resposne is  : {} ",rbtDeactivateSubscriptionStatus);
			return rbtDeactivateSubscriptionStatus ;		
		} else {
			logger.error("RBT DeactivateRbtSubscription sends error response");
			throw new ReferralChainServiceException(
						ErrorCode.RBT_ERROR,
						ErrorCode.RBT_DEACTIVATION_FAILED,
						"DeactivateRbtSubscription sends error response");  
	         }
	}	
}