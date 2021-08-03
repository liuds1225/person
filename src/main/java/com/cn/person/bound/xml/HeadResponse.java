package com.cn.person.bound.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 响应公共xml
 * 
 * @author Administrator 20210307
 *
 */
public class HeadResponse {

	/** 交易流水号 */
	private String transactionNo;
	
	/** 交易编码 */
	private String transactionCode;

	/** 响应结果码 00000-成功 11111-业务处理失败 99999-程序异常 */
	private String responseCode;

	/** 响应描述 */
	private String responseDesc;

	/** 响应时间 14位 */
	private String responseDateTime;

	@JacksonXmlProperty(localName = "TRANSACTION_NO")
	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	@JacksonXmlProperty(localName = "TRANSACTION_CODE")
	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	
	@JacksonXmlProperty(localName = "RESPONSE_CODE")
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@JacksonXmlProperty(localName = "RESPONSE_DESC")
	public String getResponseDesc() {
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

	@JacksonXmlProperty(localName = "RESPONSE_DATETIME")
	public String getResponseDateTime() {
		return responseDateTime;
	}

	public void setResponseDateTime(String responseDateTime) {
		this.responseDateTime = responseDateTime;
	}

}
