package com.act.activiti.service;

import com.act.activiti.dao.ActRequestDao;
import com.act.activiti.entity.ActRequest;
import com.act.activiti.entity.Membership;
import com.act.activiti.util.BaseTooles;
import com.act.activiti.util.DateUtil;
import com.act.activiti.util.ResCode;
import com.act.activiti.util.ResDto;
import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Created by yawn on 2018-01-08 13:44
 */
@Service
public class VacationService extends BaseTooles {

	@Resource
	private RuntimeService runtimeService;
	@Resource
	private IdentityService identityService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
	@Resource
	private MembershipService membershipService;
	@Resource
	private ProcessEngine engine;
	@Resource
	private ActRequestDao actRequestDao;
	@Resource
	public ManagementService managementService;
	
	public ResDto instertVac(JSONObject prama) {
		String userName = prama.getString("userName");
		String processDefineKey = prama.getString("processDefineKey");
		//处理模板参数
		Map<String, Object> vars = prama.getJSONObject("vaMap").getInnerMap();
		ActRequest actRequest = actRequestDao.findByParam(new ActRequest(processDefineKey, 1));
		if (actRequest != null) {
            String[] reqPram = actRequest.getRequestaram().split(",");
            System.out.println(vars);
            for (String value : reqPram) {
                if (!vars.containsKey(value) || vars.get(value) == null) {
                    return backError(ResCode.GROUP_TYPE_ISTEMPTY);
                }
            }
            String auditFlagNo = actRequest.getAuditFlagNo();

            if (StringUtils.isNotBlank(auditFlagNo)) {
                Object value = vars.get(auditFlagNo);
                if (value != null || StringUtils.isNotBlank(value.toString())) {
                    List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().variableValueEquals(auditFlagNo, value).list();
                    for (HistoricVariableInstance historicVariableInstance : list) {
                        String instance = historicVariableInstance.getProcessInstanceId();
                        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(historicVariableInstance.getProcessInstanceId()).singleResult();
                        if (pi != null) {
                            return backError(ResCode.RESUBMIT);
                        }
                        List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(instance).list();
                        if (varInstanceList != null && varInstanceList.size() > 0) {
                            for (HistoricVariableInstance hvi : varInstanceList) {
                                String key = hvi.getVariableName();
                                if (StringUtils.equals(key, "approvalStatus")) {
                                    if (StringUtils.equals("true", hvi.getValue().toString())) {
                                        //TODO 改成特定格式
                                        return backError(ResCode.RESUBMIT).setMsg("该请求已经通过审批，请勿重复审批");
                                    }
                                }

                            }

                        }
                    }
                }
            }
        }

		List<User> userList = identityService.createUserQuery().userId(userName).list();
		if (userList == null || userList.size() <= 0) {
			return backError(ResCode.USER_NAME_ISTEMPTY);
		}
		Membership membership = membershipService.find(userName);
		if (membership == null) {
			return backError(ResCode.USER_NAME_ISTEMPTY);
		}
		
		identityService.setAuthenticatedUserId(userName);
		// 开始流程
		ProcessInstance vacationInstance = runtimeService.startProcessInstanceByKey(processDefineKey);
		// 查询当前任务
		Task currentTask = taskService.createTaskQuery().processInstanceId(vacationInstance.getId()).singleResult();
		
		List<IdentityLink> links = taskService.getIdentityLinksForTask(currentTask.getId());
		//校验权限
		String groupId = membership.getGroupId();
		if (!getIdentyLink(links, groupId, membership.getUserId())) {
			return backError(ResCode.RESP_NO_PERMISSION);
		}
		// 申明任务
		taskService.claim(currentTask.getId(), userName);
		taskService.complete(currentTask.getId(), vars);
		return backSu();
	}
	
	/**
	 * 获取历史记录
	 *
	 * @param userName
	 * @return
	 */
	public ResDto myVacRecord(String userName, String processDefineKey, int firstResult, int maxResults) {
		HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefineKey).startedBy(userName).finished();
		int total = Integer.parseInt("" + historicProcessInstanceQuery.count());
		int page = firstResult == 1 ? 0 : firstResult * maxResults - maxResults;
		List<HistoricProcessInstance> hisProInstance = historicProcessInstanceQuery.orderByProcessInstanceEndTime().desc().listPage(page, maxResults);
		List<Map<String, Object>> vacList = new ArrayList<>();
		for (HistoricProcessInstance hisInstance : hisProInstance) {
			Map<String, Object> map = new HashMap<>(16);
			map.put("teskId", hisInstance.getId());
			map.put("businessKey", hisInstance.getBusinessKey());
			map.put("startUserId", hisInstance.getStartUserId());
			map.put("startTime", DateUtil.sdfLongTimePlus.format(hisInstance.getStartTime()));
			map.put("endTime", DateUtil.sdfLongTimePlus.format(hisInstance.getEndTime()));
			map.put("endActivityId", hisInstance.getEndActivityId());
			List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(hisInstance.getId()).list();
			if (varInstanceList != null && varInstanceList.size() > 0) {
				for (HistoricVariableInstance hvi : varInstanceList) {
					map.put(hvi.getVariableName(), hvi.getValue());
				}
				vacList.add(map);
			}
			
		}
		JSONObject json = new JSONObject() {{
			put("total", total);
			put("list", vacList);
			
		}};
		return backSuBasic(json);
	}
	
	/**
	 * 获取我批准的任务
	 *
	 * @param userName
	 * @return
	 */
	public ResDto myAudit(String userName, String processDefineKey, int firstResult, int maxResults) {
		TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(processDefineKey).taskCandidateUser(userName);
		int total = Integer.parseInt("" + taskQuery.count());
		int page = firstResult == 1 ? 0 : firstResult * maxResults - maxResults;
		List<Task> taskList = taskQuery.orderByTaskCreateTime().desc().listPage(page, maxResults);
		List<Map> vacTaskList = new ArrayList<>();
		for (Task task : taskList) {
			String instanceId = task.getProcessInstanceId();
			
			ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
			Map<String, Object> map = runtimeService.getVariables(instance.getId());
			map.put("applyTime", DateUtil.sdfLongTimePlus.format(instance.getStartTime()));
			map.put("applyStatus", instance.isEnded());
			map.put("id", task.getId());
			map.put("name", task.getName());
			map.put("createTime", DateUtil.sdfLongTimePlus.format(task.getCreateTime()));
			vacTaskList.add(map);
		}
		JSONObject json = new JSONObject() {{
			put("total", total);
			put("list", vacTaskList);
			
		}};
		return backSuBasic(json);
	}
	
	public ResDto passMyAudit(JSONObject prama) {
		String userName = prama.getString("userName");
		String taskId = prama.getString("taskId");
		String result = prama.getString("result");
		boolean approvalStatus = prama.getBooleanValue("approvalStatus");
		List<User> userList = identityService.createUserQuery().userId(userName).list();
		if (userList == null || userList.size() <= 0) {
			return backError(ResCode.USER_NAME_ISTEMPTY);
		}
		Membership membership = membershipService.find(userName);
		if (membership == null) {
			return backError(ResCode.USER_NAME_ISTEMPTY);
		}
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return backError(ResCode.TASK_NOTEMPTY);
		}
		List<IdentityLink> links = taskService.getIdentityLinksForTask(task.getId());
		//校验权限
		if (!getIdentyLink(links, membership.getGroupId(), membership.getUserId())) {
			return backError(ResCode.RESP_NO_PERMISSION);
		}
		//是否已经审核
		if (task.isSuspended()) {
			return backError(ResCode.ALREADY_APPROVED);
		}
		Map<String, Object> vars = new HashMap<>();
		vars.put("result", result);
		vars.put("auditor", userName);
		vars.put("auditTime", new Date());
		vars.put("approvalStatus", approvalStatus);
		taskService.claim(taskId, userName);
		List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
		JSONObject json = new JSONObject() {{
			if (varInstanceList != null && varInstanceList.size() > 0) {
				for (HistoricVariableInstance hvi : varInstanceList) {
					put(hvi.getVariableName(), hvi.getValue());
				}
			}
		}};
		taskService.complete(taskId, vars);

		return backSu();
	}
	
	
	public ResDto findMyVac(String userName, String processDefineKey, int firstResult, int maxResults) {
		ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefineKey).startedBy(userName);
		int total = Integer.parseInt("" + processInstanceQuery.count());
		int page = firstResult == 1 ? 0 : firstResult * maxResults - maxResults;
		List<ProcessInstance> instanceList = processInstanceQuery.orderByProcessInstanceId().desc().listPage(page, maxResults);
		List<Map> vacList = new ArrayList<>();
		for (ProcessInstance instance : instanceList) {
			Map<String, Object> map = runtimeService.getVariables(instance.getId());
			map.put("applyTime", DateUtil.sdfLongTimePlus.format(instance.getStartTime()));
			map.put("applyStatus", instance.isEnded());
			map.put("processInstanceId", instance.getProcessInstanceId());
			vacList.add(map);
		}
		JSONObject json = new JSONObject() {{
			put("total", total);
			put("list", vacList);
			
		}};
		return backSuBasic(json);
		
	}
	
	public boolean getIdentyLink(List<IdentityLink> links, String groupId, String userId) {
		for (IdentityLink identityLink : links) {
			String taskGroupId = identityLink.getGroupId();
			if (taskGroupId != null && !StringUtils.equals(taskGroupId, groupId)) {
				return false;
			}
			String taskUserId = identityLink.getUserId();
			if (taskUserId != null && !StringUtils.equals(taskUserId, userId)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 我的审批历史
	 *
	 * @param userName
	 * @param processDefineKey
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public ResDto findMyVacHistory(String userName, String processDefineKey, int firstResult, int maxResults) {
		HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery().processDefinitionKey(processDefineKey).taskAssignee(userName).finished();
		int total = Integer.parseInt("" + historicTaskInstanceQuery.count());
		int page = firstResult == 1 ? 0 : firstResult * maxResults - maxResults;
		List<HistoricTaskInstance> list = historicTaskInstanceQuery.orderByHistoricTaskInstanceEndTime().desc().listPage(page, maxResults);
		List<Map<String, Object>> vacList = new ArrayList<>();
		for (HistoricTaskInstance hai : list) {
			Map<String, Object> map = new HashMap<>();
			List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(hai.getProcessInstanceId()).list();
			if (varInstanceList != null && varInstanceList.size() > 0) {
				for (HistoricVariableInstance hvi : varInstanceList) {
					map.put(hvi.getVariableName(), hvi.getValue());
				}
				vacList.add(map);
			}
		}
		return backSuBasic(new JSONObject() {{
			put("total", total);
			put("list", vacList);
			
		}});
		
	}

}