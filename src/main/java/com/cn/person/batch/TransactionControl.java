package com.cn.person.batch;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cn.person.bound.xml.ResultInfo;
import com.cn.person.constant.ConstantClass;

@Component
public class TransactionControl {
	private static final Logger log = LoggerFactory.getLogger(TransactionControl.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Transactional
	public void transactionControl(String className, String methodStr, String value) throws Exception {
		Class<?> cls = Class.forName(className);
		// 获取spring中的bean对象
		Object bean = applicationContext.getBean(cls);
		Method method = cls.getMethod(methodStr, String.class);
		ResultInfo resultInfo = (ResultInfo) method.invoke(bean, value);
		// 处理失败进行数据的回滚
		if (resultInfo == null || ConstantClass.FAIL_CODE.equals(resultInfo.getResponseCode())) {
			log.info("{} {}处理失败进行数据回滚，参数为：{}", className, methodStr, value);
			throw new RuntimeException(resultInfo.getResponseDesc());
		}
	}
}
