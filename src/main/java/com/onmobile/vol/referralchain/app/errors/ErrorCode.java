package com.onmobile.vol.referralchain.app.errors;

public enum ErrorCode {
    
	REFERRAL_SERVICE_ERROR(400),
	INTERNAL_ERROR(500),
	MISSING_PARAMETER(400),
	
	PARENT_ALREADY_REGISTERED(400),
	OPERATOR_NOT_SUPPORTED(400),
    CIRCLE_NOT_SUPPORTED(400),
    MODE_NOT_SUPPORTED(400),
    MSISDN_IS_NULL(400),
    
    // Add Child API
    PARENT_SUBSCRIPTION_NOT_FOUND(400),
    CHILD_ALREADY_REGISTER_AS_PARENT(400),
    CHILD_ALREADY_ADDED(400),
    CHILD_ALREADY_ADDED_WITH_DIFFERENT_PARENT(400),
    CHILD_ACTIVE(400),
    CHILD_INVALID(400),
    
    CHILD_ADDED_ERROR(400),
    CHILD_ALREADY_REMOVED(400),
    CHILD_NOT_FOUND(400),
    CHILD_LIMIT_EXCEEDED(400),
    CHILD_STATUS_INVALID(400),
	PARSING_ERROR(400),
	
	GIFT_EXPIRED(400),
	
	RBT_ERROR(400),
	RBT_GET_SUBSCRIPTION_FAILED(400),
	RBT_DEACTIVATION_FAILED(400);
	
    private int responseStatus;
    
    ErrorCode(int responseStatus){
        this.responseStatus = responseStatus;
    }
 
    public int getResponseStatus() {
        return responseStatus;
    }       
}