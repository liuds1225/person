package com.cn.person.service.out.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cn.person.bound.xml.ResultInfo;
import com.cn.person.service.out.OutService;

@Service
public class Out10001ServiceImpl implements OutService{
	private static final Logger log = LoggerFactory.getLogger(Out10001ServiceImpl.class);

	
	@Override
	public ResultInfo submitData(String requestXml) {
		ResultInfo ResultInfo = new ResultInfo();
		return ResultInfo;
	}

}
