package com.cn.person.bound.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 请求公共xml
 * 
 * @author Administrator 20210307
 *
 */
public class HeadRequest {

	/** 交易流水号 */
	private String transactionNo;

	/** 交易编码 */
	private String transactionCode;

	/** 请求时间 14位 */
	private String requestDateTime;

	@JacksonXmlProperty(localName = "TRANSACTION_NO")
	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	@JacksonXmlProperty(localName = "RESPONSE_DATETIME")
	public String getRequestDateTime() {
		return requestDateTime;
	}

	public void setRequestDateTime(String requestDateTime) {
		this.requestDateTime = requestDateTime;
	}

	@JacksonXmlProperty(localName = "TRANSACTION_CODE")
	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	
	
}
