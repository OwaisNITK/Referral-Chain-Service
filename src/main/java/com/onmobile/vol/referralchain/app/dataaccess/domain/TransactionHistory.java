package com.onmobile.vol.referralchain.app.dataaccess.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction_history")
public class TransactionHistory {
			
	@Id
	@GeneratedValue
	@Column(name = "transaction_id")
	private int transaction_id ;
		
	@Column(name = "pmsisdn")
	private String pmsisdn ;
	
	@Column(name = "cmsisdn")
	private String cmsisdn ;
	
	@Column(name = "created", columnDefinition = "CURRENT_TIMESTAMP")
	private Date created;
	 
	@Enumerated(EnumType.STRING)
    @Column(name = "action")
    private Action action;
				
	@Column(name = "info")
	private String info ;
	
	@Column(name = "mode")
	private String mode ;

	public TransactionHistory() {}

	public int getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(int transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getPmsisdn() {
		return pmsisdn;
	}

	public void setPmsisdn(String pmsisdn) {
		this.pmsisdn = pmsisdn;
	}

	public String getCmsisdn() {
		return cmsisdn;
	}

	public void setCmsisdn(String cmsisdn) {
		this.cmsisdn = cmsisdn;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}