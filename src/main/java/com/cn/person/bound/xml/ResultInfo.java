package com.cn.person.bound.xml;

/**
 * 响应结果
 * 
 * @author Administrator 20210307
 *
 */
public class ResultInfo {

	/** 响应结果码 00000-成功 11111-失败 */
	private String responseCode;

	/** 响应描述 */
	private String responseDesc;

	/** 实体域 */
	private String requestEntity;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseDesc() {
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

	public String getRequestEntity() {
		return requestEntity;
	}

	public void setRequestEntity(String requestEntity) {
		this.requestEntity = requestEntity;
	}

}
