package com.act.activiti.service;

import com.act.activiti.util.BaseTooles;
import com.act.activiti.util.ResDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pen
 */
@Service
public class ProcdefService extends BaseTooles {
	@Resource
	private ProcessEngine engine;
	
	/**
	 * 获取审批流列表
	 *
	 * @return
	 * @throws Exception
	 */
	public ResDto getProcdef() {
		List<ProcessDefinition> list = engine.getRepositoryService().createProcessDefinitionQuery().latestVersion().orderByTenantId().desc().list();
		logger.info("打印 list:" + list);
		JSONArray array = new JSONArray();
		
		List<JSONObject> newList = new ArrayList<>();
		list.stream().forEach(processDefinition -> {
			JSONObject jsonObject = new JSONObject() {
				{
					put("key", processDefinition.getKey());
					put("name", processDefinition.getName());
				}
			};
			newList.add(jsonObject);
		});
		return backSuBasic(newList);
	}
}
