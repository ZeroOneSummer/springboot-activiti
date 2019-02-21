package com.act.activiti.util;

import java.util.HashMap;

/**
 * 用于返回JSON结果
 * @author Pen
 */
public class ResDto extends HashMap<String, Object> {
	private static final long serialVersionUID = 1382326973880892407L;
	
	public ResDto() {
		super();
	}
	
	public ResDto(int initialCapacity) {
		super(initialCapacity);
	}
	
	public ResDto(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}
	
	public ResDto setBasic(final ResCode code, final Object msg, final Object data) {
		this.put("rc", code.getCode());
		this.put("msg", msg);
		this.put("data", data);
		return this;
	}
	
	public ResDto setFalse(final ResCode code, final Object msg) {
		return setBasic(code, msg, "");
	}
	
	public ResDto setFalse(final ResCode code) {
		return setBasic(code, code.getMsg(), "");
	}
	
	public ResDto setTrue(final ResCode code, final Object msg) {
		return setBasic(code, msg, "");
	}
	
	public ResDto setTrue(final ResCode code) {
		return setBasic(code, code.getMsg(), "");
	}
	
	public ResDto setCode(final String value) {
		this.put("code", value);
		return this;
	}
	
	public ResDto setCode(final ResCode code) {
		this.put("code", code.getCode());
		this.put("msg", code.getMsg());
		return this;
	}
	
	public ResDto setDate(final Object value) {
		this.put("data", value);
		return this;
	}
	
	public ResDto setMsg(final Object value) {
		this.put("msg", value);
		return this;
	}
	
	public ResDto append(final String name, final Object value) {
		this.put(name, value);
		return this;
	}
	
}
