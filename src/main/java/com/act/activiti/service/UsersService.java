package com.act.activiti.service;

import com.act.activiti.dao.UsersDao;
import com.act.activiti.entity.Users;
import com.act.activiti.util.BaseTooles;
import com.act.activiti.util.Md5Utils;
import com.act.activiti.util.ResCode;
import com.act.activiti.util.ResDto;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Created by yawn on 2018-01-08 13:55
 */
@Service
public class UsersService extends BaseTooles {

	private String password = "111111";
	private static final String ALL = "all";
	private static final String HEX_NUMS_STR = "0123456789ABCDEF";

	@Resource
	private IdentityService identityService;

	@Resource
	private ProcessEngine engine;
	
	@Resource
	private UsersDao usersDao;
	
	public ResDto addUser(Users user) throws Exception {
		IdentityService is = engine.getIdentityService();
		UserQuery userQuery = is.createUserQuery();
		String userId = user.getUserName();
		String groupId = user.getGroupId();
		List<User> userList = userQuery.userId(userId).list();
		if (userList != null && userList.size() > 0) {
			return backError(ResCode.USER_USERID_NOTEMPTY);
		}
		List<Group> groupsList = is.createGroupQuery().groupId(user.getGroupId()).list();
		if (groupsList == null || groupsList.size() <= 0) {
			return backError(ResCode.GROUP_ID_ISEMPTY);
		}
		//保存用户信息
		User actUser = is.newUser(userId);
		String pass = user.getPassword();
		if (ckBlank(pass)) {
			pass = password;
		}
		pass = Md5Utils.encryptPassword(pass, HEX_NUMS_STR);
		actUser.setPassword(pass);
		identityService.saveUser(actUser);
		//保存关系
		is.createMembership(user.getUserName(), groupId);
		return backSu();
	}
	
	
	/**
	 * 获取用户信息
	 *
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public ResDto getUser(String userName) {
		Users users = new Users();
		users.setUserName(userName);
		return backSuBasic(usersDao.findByParam(users));
	}
	
	/**
	 * 获取用户列表
	 *
	 * @param groupType
	 * @return
	 * @throws Exception
	 */
	public ResDto getUserList(String groupType) {
		Users users = new Users();
		users.setGroupId(groupType);
		return backSuBasic(usersDao.findListByParam(users));
	}
	
	/**
	 * 获取用户列表
	 *
	 * @param prama
	 * @return
	 * @throws Exception
	 */
	public ResDto upUsersGroup(JSONObject prama) {
		try {
			String userName = prama.getString("userName");
			String newGroupId = prama.getString("newGroupId");
			Users users = usersDao.findByParam(new Users(userName));
			if (users == null) {
				return backError(ResCode.USER_USERID_ISEMPTY);
			}
			String groupId = users.getGroupId();
			if (StringUtils.equals(newGroupId, users.getGroupId())) {
				return backError(ResCode.USER_USERID_NOTEMPTY);
			}
			IdentityService is = engine.getIdentityService();
			Group group = is.createGroupQuery().groupId(newGroupId).singleResult();
			if (group == null) {
				return backError(ResCode.GROUP_NAME_ISTEMPTY);
			}
			
			//删除关系
			is.deleteMembership(userName, groupId);
			//绑定关系
			is.createMembership(userName, newGroupId);
			return backSu();
		} catch (Exception e) {
			e.printStackTrace();
			return backError();
		}
		
	}
}
