package com.act.activiti.controller;

import com.act.activiti.entity.UserGroup;
import com.act.activiti.service.GroupService;
import com.act.activiti.util.BaseTooles;
import com.act.activiti.util.ResCode;
import com.act.activiti.util.ResDto;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Created by yawn on 2018-01-11 10:02
 */
@RestController
public class GroupController extends BaseTooles {
	
	@Resource
	GroupService groupService;
	
	/**
	 * 添加分组
	 *
	 * @return
	 */
	@PostMapping( "/addGroup" )
	public ResDto addGroup(@RequestBody UserGroup group) {
		if (ckBlank(group.getId())) {
			return backError(ResCode.GROUP_ID_ISEMPTY);
		}
		if (ckBlank(group.getName())) {
			return backError(ResCode.GROUP_NAME_ISTEMPTY);
		}
		if (ckBlank(group.getType())) {
			return backError(ResCode.GROUP_TYPE_ISTEMPTY);
		}
		return groupService.instertGroup(group);
	}
	
	/**
	 * 添加分组
	 *
	 * @return
	 */
	@GetMapping( "/getGroup" )
	public ResDto addGroup(@RequestParam( "groupType" ) String groupType) {
		if (ckBlank(groupType)) {
			return backError(ResCode.TYPE_ISEMPTY);
		}
		return groupService.getGroup(groupType);
	}
	
}
