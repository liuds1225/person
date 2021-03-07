package com.cn.person.bound.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * 请求公共xml
 * 
 * @author Administrator 20210307
 *
 */
public class RequestInfo {

	private HeadRequest headRequest;
	
	/** 实体域 */
	private String requestEntity;

	@JacksonXmlProperty(localName = "TX_HEAD")
	public HeadRequest getHeadRequest() {
		return headRequest;
	}

	public void setHeadRequest(HeadRequest headRequest) {
		this.headRequest = headRequest;
	}

	@JacksonXmlProperty(localName = "ENTITY")
	public String getRequestEntity() {
		return requestEntity;
	}

	public void setRequestEntity(String requestEntity) {
		this.requestEntity = requestEntity;
	}
	
	
	
}
