package com.cn.person.bound;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.person.bound.xml.HeadRequest;
import com.cn.person.bound.xml.HeadResponse;
import com.cn.person.bound.xml.RequestInfo;
import com.cn.person.bound.xml.ResponseInfo;
import com.cn.person.bound.xml.ResultInfo;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 公共接口接入类
 * 
 * @author Administrator 20210307
 *
 */
@Controller
public class InboundService {
	private static final Logger log = LoggerFactory.getLogger(InboundService.class);

	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * 测试接口接入
	 * 
	 * @return
	 */
	@RequestMapping("/")
	public String testIndex() {
		log.info("欢迎登录个人服务系统客户端。。。");
		return "index";
	}

	/**
	 * 公共接口接入类
	 * 
	 * @return
	 */
	@RequestMapping(value = "/request", method = RequestMethod.POST)
	@ResponseBody
	public ResponseInfo inbound(@RequestParam("requestXml") String requestXml) {
		XmlMapper xmlmapper = new XmlMapper();
		HeadRequest headRequest = new HeadRequest();
		String startDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		try {
			log.info("请求报文：{}", requestXml);
			if (StringUtils.isEmpty(requestXml) || !requestXml.startsWith("<TX>")) {
				return getResponseXml(headRequest, "", "99999", "请求报文解析发生异常");
			}
			RequestInfo requestInfo = xmlmapper.readValue(requestXml, RequestInfo.class);
			if (requestInfo == null || requestInfo.getHeadRequest() == null) {
				return getResponseXml(headRequest, "", "99999", "请求报文解析发生异常");
			}
			headRequest = requestInfo.getHeadRequest();
			if (StringUtils.isEmpty(headRequest.getTransactionNo())) {
				return getResponseXml(headRequest, "", "99999", "交易流水号不能为空");
			}
			if (StringUtils.isEmpty(headRequest.getTransactionCode())) {
				return getResponseXml(headRequest, "", "99999", "交易编码不能为空");
			}
			if (StringUtils.isEmpty(headRequest.getRequestDateTime())) {
				return getResponseXml(headRequest, "", "99999", "交易时间不能为空");
			}
			if (headRequest.getRequestDateTime().length() != 14) {
				return getResponseXml(headRequest, "", "99999", "交易时间格式不正确");
			}
			if (StringUtils.isEmpty(requestInfo.getRequestEntity())) {
				return getResponseXml(headRequest, "", "99999", "交易请求参数不能为空");
			}
			// 获取class对象
			Class<?> cls = Class
					.forName("com.cn.person.service.out.impl.Out" + headRequest.getTransactionCode() + "ServiceImpl");
			// 获取spring中的bean对象
			Object bean = applicationContext.getBean(cls);
			Method method = cls.getMethod("submitData", String.class);
			ResultInfo resultInfo = (ResultInfo) method.invoke(bean, requestInfo.getRequestEntity());
			return getResponseXml(headRequest, resultInfo.getRequestEntity(), resultInfo.getResponseCode(),
					resultInfo.getResponseDesc());
		} catch (Exception e) {
			log.info("请求报文解析异常：{}", e);
			return getResponseXml(headRequest, "", "99999", "请求报文解析发生异常");
		} finally {
			String endDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			log.info("响应处理结果介绍：00000-交易成功 11111-交易失败 99999-程序异常");
			log.info("请求开始时间：{}, 请求结束时间：{}", startDateTime, endDateTime);
		}
	}

	/**
	 * 得到响应报文
	 * 
	 * @param requestXml
	 * @param entityXml
	 * @param responseCode
	 * @param responseDesc
	 * @return
	 */
	private ResponseInfo getResponseXml(HeadRequest headRequest, String entityXml, String responseCode,
			String responseDesc) {
		ResponseInfo responseInfo = new ResponseInfo();
		HeadResponse headResponse = new HeadResponse();
		headResponse.setTransactionNo(headRequest.getTransactionNo());
		// 按约定，返回不能是空的
		if (StringUtils.isEmpty(responseCode)) {
			headResponse.setResponseCode("99999");
			headResponse.setResponseDesc("请求交易处理发生异常");
		} else {
			headResponse.setResponseCode(responseCode);
			headResponse.setResponseDesc(responseDesc);
		}
		headResponse.setResponseDateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
		responseInfo.setHeadResponse(headResponse);
		responseInfo.setResponseEntity(entityXml);
		return responseInfo;
	}

}
