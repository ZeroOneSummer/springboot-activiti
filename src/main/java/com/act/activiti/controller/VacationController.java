package com.act.activiti.controller;

import com.act.activiti.service.VacationService;
import com.act.activiti.util.BaseTooles;
import com.act.activiti.util.ResCode;
import com.act.activiti.util.ResDto;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Pen
 */
@RestController
public class VacationController extends BaseTooles {

	@Resource
	private VacationService vacationService;

	@Resource
	private VacationService va;

	/**
	 * 获取申请中订单
	 *
	 * @param userName
	 * @return ResDto
	 */
	@GetMapping( "/getMyVac" )
	public ResDto findMyVac(@RequestParam( "userName" ) String userName, @RequestParam( "processDefineKey" ) String processDefineKey, @RequestParam( "page" ) int page, @RequestParam( "limit" ) int limit) {
		if (ckBlank(userName)) {
			return backError(ResCode.RESP_705);
		}
		if (ckBlank(processDefineKey)) {
			return backError(ResCode.PROCESSDEFINEKEY_NOTEMPTY);
		}
		if (ckBlank(page)) {
			return backError(ResCode.PAGE_ISEMPTY);
		}
		if (ckBlank(limit)) {
			return backError(ResCode.LIMIT_ISEMPTY);
		}
		return vacationService.findMyVac(userName, processDefineKey, page, limit);
	}

	/**
	 * 获取我审批的订单
	 *
	 * @param userName
	 * @param processDefineKey
	 * @return
	 */
	@GetMapping( "/getMyAudit" )
	public Object getMyAudit(@RequestParam( "userName" ) String userName, @RequestParam( "processDefineKey" ) String processDefineKey, @RequestParam( "page" ) int page, @RequestParam( "limit" ) int limit) {
		if (ckBlank(userName)) {
			return backError(ResCode.RESP_705);
		}
		if (ckBlank(processDefineKey)) {
			return backError(ResCode.PROCESSDEFINEKEY_NOTEMPTY);
		}
		if (ckBlank(page)) {
			return backError(ResCode.PAGE_ISEMPTY);
		}
		if (ckBlank(limit)) {
			return backError(ResCode.LIMIT_ISEMPTY);
		}
		return vacationService.myAudit(userName, processDefineKey, page, limit);
	}

	/**
	 * 获取我审批的订单
	 *
	 * @param userName
	 * @param processDefineKey
	 * @return
	 */
	@GetMapping( "/getMyVacHistory" )
	public Object getMyVacHistory(@RequestParam( "userName" ) String userName, @RequestParam( "processDefineKey" ) String processDefineKey, @RequestParam( "page" ) int page, @RequestParam( "limit" ) int limit) {
		if (ckBlank(userName)) {
			return backError(ResCode.RESP_705);
		}
		if (ckBlank(processDefineKey)) {
			ResDto resDto = backError(ResCode.PROCESSDEFINEKEY_NOTEMPTY);
			return resDto;
		}
		if (ckBlank(page)) {
			return backError(ResCode.PAGE_ISEMPTY);
		}
		if (ckBlank(limit)) {
			return backError(ResCode.LIMIT_ISEMPTY);
		}
		return vacationService.findMyVacHistory(userName, processDefineKey, page, limit);
	}


	/**
	 * 审批订单订单
	 *
	 * @param prama
	 * @return ResDto
	 */
	@PostMapping( "/passMyAudit" )
	public ResDto passMyAudit(@RequestBody JSONObject prama) {
		if (ckBlank(prama.getString("userName"))) {
			return backError(ResCode.RESP_705);
		}
		if (ckBlank(prama.getString("taskId"))) {
			return backError(ResCode.TASK_ID_NOTEMPTY);
		}
		return vacationService.passMyAudit(prama);
	}

	/**
	 * 获取已完成历史申请记录
	 *
	 * @param userName
	 * @return
	 */
	@GetMapping( "/getMyVacRecord" )
	ResDto myVacRecord(@RequestParam( "userName" ) String userName, @RequestParam( "processDefineKey" ) String processDefineKey, @RequestParam( "page" ) int page, @RequestParam( "limit" ) int limit) {
		if (ckBlank(userName)) {
			return backError(ResCode.RESP_705);
		}
		if (ckBlank(processDefineKey)) {
			return backError(ResCode.PROCESSDEFINEKEY_NOTEMPTY);
		}
		if (ckBlank(page)) {
			return backError(ResCode.PAGE_ISEMPTY);
		}
		if (ckBlank(limit)) {
			return backError(ResCode.LIMIT_ISEMPTY);
		}
		return vacationService.myVacRecord(userName, processDefineKey, page, limit);
	}

	/**
	 * 添加任务
	 *
	 * @param prama
	 * @return
	 */
	@PostMapping( "/addVac" )
	ResDto addVac(@RequestBody JSONObject prama) {
		if (ckBlank(prama)) {
			return backError();
		}
		if (ckBlank(prama.getString("userName"))) {
			return backError(ResCode.RESP_705);
		}
		if (ckBlank(prama.getString("processDefineKey"))) {
			return backError(ResCode.PROCESSDEFINEKEY_NOTEMPTY);
		}
		return va.instertVac(prama);
	}
}
