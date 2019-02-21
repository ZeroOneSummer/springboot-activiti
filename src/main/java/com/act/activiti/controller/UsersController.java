package com.act.activiti.controller;

import com.act.activiti.entity.Users;
import com.act.activiti.service.UsersService;
import com.act.activiti.util.BaseTooles;
import com.act.activiti.util.ResCode;
import com.act.activiti.util.ResDto;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Pen
 */
@RestController
public class UsersController extends BaseTooles {

	@Resource
	UsersService usersService;

	@Resource
	private ProcessEngineConfiguration configuration;

	@Resource
	private ProcessEngine engine;
	
	@PostMapping( "/addUsers" )
	public Object addUser(@RequestBody Users users) {
		if (ckBlank(users.getUserName())) {
			return backError(ResCode.USER_NAME_ISTEMPTY);
		}
		if (ckBlank(users.getGroupId())) {
			return backError(ResCode.GROUP_ID_ISEMPTY);
		}
		try {
			return usersService.addUser(users);
		} catch (Exception e) {
			e.printStackTrace();
			return backError();
		}
	}
	
	@GetMapping( "/getUsers" )
	public ResDto getUsers(@RequestParam( "userName" ) String userName) {
		if (ckBlank(userName)) {
			return backError(ResCode.USER_NAME_ISTEMPTY);
		}
		try {
			return usersService.getUser(userName);
		} catch (Exception e) {
			e.printStackTrace();
			return backError();
		}
	}
	
	@GetMapping( "/getUsersList" )
	public ResDto getUsersList(@RequestParam( "groupType" ) String groupType) {
		if (ckBlank(groupType)) {
			return backError(ResCode.TYPE_ISEMPTY);
		}
		try {
			return usersService.getUserList(groupType);
		} catch (Exception e) {
			e.printStackTrace();
			return backError();
		}
	}
	
	/**
	 * 修改分组
	 *
	 * @param groupType
	 * @return
	 */
	@PostMapping( "/upUsersGroup" )
	public ResDto upUsersGroup(@RequestBody JSONObject prama) {
		if (ckBlank(prama)) {
			return backError(ResCode.GROUP_TYPE_ISTEMPTY);
		}
		if (ckBlank(prama.getString("userName"))) {
			return backError(ResCode.USER_NAME_ISTEMPTY);
		}
		if (ckBlank(prama.getString("newGroupId"))) {
			return backError(ResCode.GROUP_ID_ISEMPTY);
		}
		try {
			return usersService.upUsersGroup(prama);
		} catch (Exception e) {
			e.printStackTrace();
			return backError();
		}
	}
	
}
