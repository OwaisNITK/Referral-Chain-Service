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

public class RbtSubscriptionResponseHandler<T> implements ResponseHandler<RbtSubscriptionResponseDto> {

	public static Logger logger  = LogManager.getLogger(RbtSubscriptionResponseHandler.class); 
	
	@Override
	public RbtSubscriptionResponseDto handleResponse(HttpResponse rbtResponse) throws ClientProtocolException, IOException {
		 int statusCode = rbtResponse.getStatusLine().getStatusCode();
		 
		 RbtSubscriptionResponseDto response = new RbtSubscriptionResponseDto();
		 logger.debug("Rbt resposne status is  : {} ",statusCode);
		 if (statusCode==200) {
			// Parse JSON Data and Prepare RbtSubscriptionResponseDto 
			HttpEntity responseEntity = rbtResponse.getEntity();
			byte[] jsonData = EntityUtils.toByteArray(responseEntity);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode arrayNode = objectMapper.readTree(jsonData);
				
			for (JsonNode subscription : arrayNode) {
				JsonNode status = subscription.path("status");
				response.setStatus(status.asText());
					
				JsonNode blacklist_info = subscription.path("blacklist_info");
				JsonNode isBlackListed = blacklist_info.path("is_blacklisted");
				response.setBlacklisted(isBlackListed.asBoolean());
			}
			logger.debug("Rbt resposne is  : {} ",response.toString());
			 return response ;
		 } else {
			 logger.error("RBT GetRbtSubscription sends error response");
			throw new ReferralChainServiceException(
					ErrorCode.RBT_ERROR,
					ErrorCode.RBT_GET_SUBSCRIPTION_FAILED,
					"GetRbtSubscription sends error response");  
         }
     }
}