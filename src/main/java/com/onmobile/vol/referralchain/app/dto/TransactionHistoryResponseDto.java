package com.onmobile.vol.referralchain.app.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onmobile.vol.referralchain.app.constants.FieldNameConstants;

public class TransactionHistoryResponseDto {

	@JsonProperty(FieldNameConstants.OFFSET)
	private int offset ;
	
	@JsonProperty(FieldNameConstants.TOTAL_COUNT)
	private long totalCount;
	
	@JsonProperty(FieldNameConstants.ITEM_COUNT)
	private int itemCount;
	
	@JsonProperty(FieldNameConstants.TRANSACTIONS)
	private List<TransactionBaseDto> transactions;
	
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public List<TransactionBaseDto> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<TransactionBaseDto> transactions) {
		this.transactions = transactions;
	}
	
	@Override
	public String toString() {
		return "TransactionHistoryResponse [offset=" + offset + ", totalCount=" + totalCount + ", itemCount="
				+ itemCount + ", transactions=" + transactions + "]";
	}	
}