package com.act.activiti.util;

/**
 * @author pen
 */

public enum ResCode {
	
	RESP_200(200, "SUCCESS"),
	RESP_500(-500, "ERROR!"),
	TYPE_ISEMPTY(-101, "查询类型参数不正确"),
	GROUP_ID_ISEMPTY(-1001, "组ID参数不正确"),
	GROUP_ID_NOTEMPTY(-1002, "组ID参数已经存在"),
	GROUP_NAME_ISTEMPTY(-1003, "组名称参数不正确"),
	GROUP_NAME_NOTEMPTY(-1004, "组名称参数已经存在"),
	GROUP_TYPE_ISTEMPTY(-1005, "类型参数不正确"),
	GROUP_TYPE_NOTEMPTY(-1006, "类型参数数已经存在"),
	USER_USERID_ISEMPTY(-1007, "用户ID不存在"),
	USER_USERID_NOTEMPTY(-1008, "用户ID已经存在"),
	USER_NAME_ISTEMPTY(-1009, "用户账号参数不正确"),
	USER_NAME_NOTEMPTY(-1010, "用户账号参数不正确"),
	TASK_ID_NOTEMPTY(-1011, "taskId参数不正确"),
	TASK_NOTEMPTY(-1011, "申请不存在"),
	RESP_705(-2705, "用户ID为空"),
	RESP_NO_PERMISSION(-1, "权限不足"),
	PROCESSDEFINEKEY_NOTEMPTY(-2, "申请类型为空"),
	ALREADY_APPROVED(-3, "已经审批"),
	PAGE_ISEMPTY(-4, "page必须大于0"),
	LIMIT_ISEMPTY(-4, "LIMIT必须大于0"),
	RESUBMIT(-2706,"重复提交申请");

	private int code;
	private String msg;
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String codemsg) {
		this.msg = codemsg;
	}
	
	ResCode(int code, String msg) {
		setCode(code);
		setMsg(msg);
	}
}
