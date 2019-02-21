package com.act.activiti.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author Created by yawn on 2018-01-08 16:44
 */
@Data
public class Vacation {
	
	/**
	 * 申请人
	 */
	private String applyUser;
	private int days;
	private String reason;
	private Date applyTime;
	private String applyStatus;
	
	/**
	 * 审核人
	 */
	private String auditor;
	private String result;
	private Date auditTime;

}
