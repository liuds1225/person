package com.cn.person.service.out.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cn.person.bound.xml.ResultInfo;
import com.cn.person.service.out.Out10001Service;

@Service
public class Out10001ServiceImpl implements Out10001Service{
	private static final Logger log = LoggerFactory.getLogger(Out10001ServiceImpl.class);

	
	@Override
	public ResultInfo submitData(String requestXml) {
		ResultInfo ResultInfo = new ResultInfo();
		return ResultInfo;
	}

}
