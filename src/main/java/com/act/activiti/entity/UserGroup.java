package com.act.activiti.entity;

import lombok.Data;

/**
 * @author Pen
 */
@Data
public class UserGroup {

	private String id;	 //组id
	private String name; //名
	private Integer rev;
	private String type; //角色类型
}
