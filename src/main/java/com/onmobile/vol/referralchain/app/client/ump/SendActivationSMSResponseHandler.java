package com.onmobile.vol.referralchain.app.client.ump;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import com.onmobile.vol.referralchain.app.dataaccess.domain.SMSStatus;

public class SendActivationSMSResponseHandler<T> implements ResponseHandler<SMSStatus> {

	@Override
	public SMSStatus handleResponse(HttpResponse umpResponse) throws ClientProtocolException, IOException {
		int statusCode = umpResponse.getStatusLine().getStatusCode();
		
		if (statusCode==200) {
			return SMSStatus.SENT ;		
		} else {
			return SMSStatus.PENDING ;
		}
	}
}