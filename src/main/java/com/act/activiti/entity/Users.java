package com.act.activiti.entity;

import lombok.Data;

/**
 * @author Pen
 */
@Data
public class Users {
	/**
	 * 用户账号
	 */
	private String userName;
	/**
	 * 用户密码
	 */
	private String password;
	/**
	 * 组id
	 */
	private String groupId;
	/**
	 * 组name
	 */
	private String groupName;
	/**
	 * 是否有效
	 */
	private String type;

	public Users() {
	}

	public Users(String userName) {
		this.userName = userName;
	}
}
