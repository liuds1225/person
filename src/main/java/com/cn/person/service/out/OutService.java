package com.cn.person.service.out;

import com.cn.person.bound.xml.ResultInfo;

/**
 * 查询用户信息
 * @author Administrator
 *
 */
public interface OutService {

	ResultInfo submitData(String requestXml);
}
