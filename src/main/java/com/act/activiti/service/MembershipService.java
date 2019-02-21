package com.act.activiti.service;

import com.act.activiti.entity.Membership;

/**
 * 组管理
 * @author Pen
 */
public interface MembershipService {
	/**
	 * @param userId
	 * @return
	 */
	Membership find(String userId);
}