package com.cn.person.bound;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.person.batch.TransactionControl;
import com.cn.person.bound.xml.HeadRequest;
import com.cn.person.bound.xml.HeadResponse;
import com.cn.person.bound.xml.RequestInfo;
import com.cn.person.bound.xml.ResponseInfo;
import com.cn.person.bound.xml.ResultInfo;
import com.cn.person.constant.ConstantClass;
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

	private static final String IP_UTILS_FLAG = ",";
	private static final String UNKNOWN = "unknown";
	private static final String LOCALHOST_IP = "0:0:0:0:0:0:0:1";
	private static final String LOCALHOST_IP1 = "127.0.0.1";

	@Autowired
	private TransactionControl batchTransactionControl;

	/**
	 * 测试接口接入
	 * 
	 * @return
	 */
	@RequestMapping("/")
	public String testIndex(HttpServletRequest request) {
		String ip = null;
		try {
			// 以下两个获取在k8s中，将真实的客户端IP，放到了x-Original-Forwarded-For。而将WAF的回源地址放到了
			// x-Forwarded-For了。
			ip = request.getHeader("X-Original-Forwarded-For");
			if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("X-Forwarded-For");
			}
			// 获取nginx等代理的ip
			if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("x-forwarded-for");
			}
			if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			// 兼容k8s集群获取ip
			if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
				if (LOCALHOST_IP1.equalsIgnoreCase(ip) || LOCALHOST_IP.equalsIgnoreCase(ip)) {
					// 根据网卡取本机配置的IP
					InetAddress iNet = null;
					try {
						iNet = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						ip = "";
					}
					ip = iNet.getHostAddress();
				}
			}
		} catch (Exception e) {
			ip = "";
		}
		// 使用代理，则获取第一个IP地址
		if (!StringUtils.isEmpty(ip) && ip.indexOf(IP_UTILS_FLAG) > 0) {
			ip = ip.substring(0, ip.indexOf(IP_UTILS_FLAG));
		}
		if (StringUtils.isEmpty(ip)) {
			ip = LOCALHOST_IP1;
		}
		log.info("欢迎IP {}登录个人服务系统。。。", ip);
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
				return getResponseXml(headRequest, "", ConstantClass.EXCEPTION_CODE, "请求报文解析发生异常");
			}
			RequestInfo requestInfo = xmlmapper.readValue(requestXml, RequestInfo.class);
			if (requestInfo == null || requestInfo.getHeadRequest() == null) {
				return getResponseXml(headRequest, "", ConstantClass.EXCEPTION_CODE, "请求报文解析发生异常");
			}
			headRequest = requestInfo.getHeadRequest();
			if (StringUtils.isEmpty(headRequest.getTransactionNo())) {
				return getResponseXml(headRequest, "", ConstantClass.EXCEPTION_CODE, "交易流水号不能为空");
			}
			if (StringUtils.isEmpty(headRequest.getTransactionCode())) {
				return getResponseXml(headRequest, "", ConstantClass.EXCEPTION_CODE, "交易编码不能为空");
			}
			if (StringUtils.isEmpty(headRequest.getRequestDateTime())) {
				return getResponseXml(headRequest, "", ConstantClass.EXCEPTION_CODE, "交易时间不能为空");
			}
			if (headRequest.getRequestDateTime().length() != 14) {
				return getResponseXml(headRequest, "", ConstantClass.EXCEPTION_CODE, "交易时间格式不正确");
			}
			if (StringUtils.isEmpty(requestInfo.getRequestEntity())) {
				return getResponseXml(headRequest, "", ConstantClass.EXCEPTION_CODE, "交易请求参数不能为空");
			}
			String className = "com.cn.person.service.out.impl.Out" + headRequest.getTransactionCode() + "ServiceImpl";
			ResultInfo resultInfo = new ResultInfo();
			try {
				batchTransactionControl.transactionControl(className, "submitData", requestInfo.getRequestEntity());
				resultInfo.setResponseCode(ConstantClass.SUCCESS_CODE);
				resultInfo.setResponseDesc("交易成功");
			} catch (RuntimeException e) {
				resultInfo.setResponseCode(ConstantClass.FAIL_CODE);
				resultInfo.setResponseDesc(e.getMessage());
			} catch (Exception e) {
				resultInfo.setResponseCode(ConstantClass.EXCEPTION_CODE);
				resultInfo.setResponseDesc(e.getMessage());
			}
			return getResponseXml(headRequest, resultInfo.getRequestEntity(), resultInfo.getResponseCode(),
					resultInfo.getResponseDesc());
		} catch (Exception e) {
			log.info("请求报文解析异常：{}", e);
			return getResponseXml(headRequest, "", ConstantClass.EXCEPTION_CODE, "请求报文解析发生异常");
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
			headResponse.setResponseCode(ConstantClass.EXCEPTION_CODE);
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
