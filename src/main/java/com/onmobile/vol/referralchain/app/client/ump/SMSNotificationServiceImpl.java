package com.onmobile.vol.referralchain.app.client.ump;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.onmobile.vol.referralchain.app.client.HttpClientFactory;
import com.onmobile.vol.referralchain.app.config.ProviderDetailsResolver;
import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;

@Component
public class SMSNotificationServiceImpl implements SMSNotificationService {
	
	@Autowired
	private ProviderDetailsResolver providerDetailsResolver ;
	
	@Autowired
	private HttpClientFactory httpClientFactory ;
	
	@Override
	public SMSStatus sendActivationSMS(String pmsisdn, String cmsisdn, String providerName) {
				
		String uri = prepareUmpURI(pmsisdn,cmsisdn,providerName);
		HttpGet request = new HttpGet(uri);
		CloseableHttpClient client = httpClientFactory.createHttpClient();
		
		SMSStatus response = SMSStatus.PENDING ;
			
		try {
				response = client.execute(request, new SendActivationSMSResponseHandler<SMSStatus>());
		} catch (Exception e) {
			// Do Nothing , Simply Send SMS Pending Response
		} 
		
		return response ;
	}
	
	private String prepareUmpURI(String pmsisdn, String cmsisdn, String providerName) {
		
		String umpUrl = providerDetailsResolver.resolveUmpUrlForProvider(providerName);
		String senderNo = providerDetailsResolver.resolveSenderNoForProvider(providerName);
		String defaultMessage = providerDetailsResolver.resolveDefaultUMPMessageForProvider(providerName);
		defaultMessage = defaultMessage.replaceAll(Pattern.quote("$[PMSISDN]"), pmsisdn);
		
		umpUrl = umpUrl.replaceAll(Pattern.quote("$[SOURCE]"), senderNo);
		umpUrl = umpUrl.replaceAll(Pattern.quote("$[TARGET]"), cmsisdn);
		umpUrl = umpUrl.replaceAll(Pattern.quote("$[MESSAGE]"), encode(defaultMessage));
			
		return umpUrl ;
	}

	private String encode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (Throwable e) {
			//TODO: Add Logger "Cannot Encode Value = " + value ;
			return value;
		}
	}
}