package com.onmobile.vol.referralchain.app.client.rbt;

public class RbtSubscriptionResponseDto {

	private String status ;
	private boolean isBlacklisted ;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isBlacklisted() {
		return isBlacklisted;
	}
	public void setBlacklisted(boolean isBlacklisted) {
		this.isBlacklisted = isBlacklisted;
	}
	@Override
	public String toString() {
		return "RbtSubscriptionResponseDto [status=" + status + ", isBlacklisted=" + isBlacklisted + "]";
	}
	
	
}