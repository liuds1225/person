package com.cn.person.bound.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 响应公共xml
 * 
 * @author Administrator 20210307
 *
 */
@JacksonXmlRootElement(localName = "TX")
public class ResponseInfo {

	/** 响应头 */
	private HeadResponse headResponse;

	/** 实体域 */
	private String responseEntity;

	@JacksonXmlProperty(localName = "TX_HEAD")
	public HeadResponse getHeadResponse() {
		return headResponse;
	}

	public void setHeadResponse(HeadResponse headResponse) {
		this.headResponse = headResponse;
	}

	@JacksonXmlProperty(localName = "ENTITY")
	public String getResponseEntity() {
		return responseEntity;
	}

	public void setResponseEntity(String responseEntity) {
		this.responseEntity = responseEntity;
	}
}
