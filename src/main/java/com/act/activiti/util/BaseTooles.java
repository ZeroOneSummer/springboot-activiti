package com.act.activiti.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author pen
 */
public class BaseTooles {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected static ResDto backError(final ResCode code) {
		return new ResDto().setFalse(code);
	}
	
	protected static ResDto backError() {
		return new ResDto().setFalse(ResCode.RESP_500);
	}
	
	protected static ResDto backError(final String msg) {
		return new ResDto().setFalse(ResCode.RESP_500).setMsg(msg);
	}
	
	protected static ResDto backErr(final Object obj) {
		return new ResDto().setFalse(ResCode.RESP_500).setDate(obj);
	}

	protected static ResDto backBasic(final ResCode code, final String msg, final Object obj) {
		return new ResDto().setBasic(code, msg, obj);
	}
	
	protected static ResDto backSuBasic(final Object obj) {
		return new ResDto().setTrue(ResCode.RESP_200).setDate(obj);
	}
	
	protected static ResDto backSu() {
		return backSu(ResCode.RESP_200);
	}
	
	protected static ResDto backSu(final ResCode code) {
		return new ResDto().setTrue(code);
	}

	protected static boolean ckBlank(final String value) {
		return StringUtils.isBlank(value);
	}

	protected static boolean ckBlank(final int value) {
		return value <= 0;
	}

	protected static boolean ckBlank(final JSONObject value) {
		return value == null || value.isEmpty();
	}

	protected static <T> T toJavaModel(final String value, Class<T> clazz) {
		return JSONObject.toJavaObject(JSONObject.parseObject(value), clazz);
	}
	
	/**
	 * 检查手机号是否 11位
	 */
	protected static boolean checkMoble(final String value) {
		if (value == null) {
			return true;
		}
		return value.length() != 11;
	}
	
	/**
	 * 检查UUID是否存在，如果不存在，自动生成
	 */
	protected static String checkUUID(final String value) {
		if (value == null || !"".equals(value)) {
			return "US-" + UUID.randomUUID().toString();
		}
		return value;
	}
	
}
