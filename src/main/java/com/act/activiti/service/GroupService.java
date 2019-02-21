package com.act.activiti.service;

import com.act.activiti.entity.UserGroup;
import com.act.activiti.util.BaseTooles;
import com.act.activiti.util.ResCode;
import com.act.activiti.util.ResDto;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 组管理
 * @author Pen
 */
@Service
public class GroupService extends BaseTooles {

	private static final String ALL = "all";

	@Resource
	private ProcessEngine engine;
	
	/**
	 * 添加组
	 *
	 * @param userGroup
	 * @return ResDto
	 */
	public ResDto instertGroup(UserGroup userGroup) {
		try {
			IdentityService identityService = engine.getIdentityService();
			GroupQuery groupQuery = identityService.createGroupQuery().groupId(userGroup.getId());
			Group group = groupQuery.singleResult();
			if (group != null) {
				return backError(ResCode.GROUP_ID_NOTEMPTY);
			}
			Group newGroup = identityService.newGroup(userGroup.getId());
			newGroup.setName(userGroup.getName());
			newGroup.setType(userGroup.getType());
			identityService.saveGroup(newGroup);
		} catch (Exception e) {
			e.printStackTrace();
			return backError();
		}
		return backSu();
	}
	
	/**
	 * 添加组
	 *
	 * @param groupType
	 * @return ResDto
	 */
	public ResDto getGroup(String groupType) {
		try {
			IdentityService identityService = engine.getIdentityService();
			List<Group> list = null;
			if (StringUtils.equals(groupType, ALL)) {
				list = identityService.createGroupQuery().list();
			} else {
				list = identityService.createGroupQuery().groupId(groupType).list();
			}
			return backSuBasic(list);
		} catch (Exception e) {
			e.printStackTrace();
			return backError();
		}
	}

}