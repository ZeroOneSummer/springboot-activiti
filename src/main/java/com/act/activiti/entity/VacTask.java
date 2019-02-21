package com.act.activiti.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author Created by yawn on 2018-01-09 14:31
 */
@Data
public class VacTask {
	
	private String id;
	private String name;
	private Vacation vac;
	private Date createTime;
}
