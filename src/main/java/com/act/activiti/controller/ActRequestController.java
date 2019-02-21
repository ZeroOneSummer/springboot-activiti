package com.act.activiti.controller;

import com.act.activiti.entity.ActRequest;
import com.act.activiti.service.ActRequestService;
import com.act.activiti.util.BaseTooles;
import com.act.activiti.util.ResCode;
import com.act.activiti.util.ResDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Created by yawn on 2018-01-11 10:02
 */
@RestController
public class ActRequestController extends BaseTooles {
	
	@Resource
	private ActRequestService actRequestService;
	
	/**
	 * 查询请求信息
	 *
	 * @return
	 */
	@GetMapping( "/getActRequest" )
	public ResDto getActRequest(@RequestParam( "processDefineKey" ) String processDefineKey) {
		if (ckBlank(processDefineKey)) {
			return backError(ResCode.PROCESSDEFINEKEY_NOTEMPTY);
		}
		return actRequestService.findByParam(new ActRequest(processDefineKey, 1));
	}
}
