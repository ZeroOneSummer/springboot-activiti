package com.act.activiti.controller;

import com.act.activiti.service.ProcdefService;
import com.act.activiti.util.ResDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author pen
 */
@RestController
public class ProcdefController {
	
	@Resource
	ProcdefService procdefService;
	
	/**
	 * 获取审批流
	 *
	 * @return
	 */
	@GetMapping( "/getProcdef" )
	public ResDto getProcdef() {
		return procdefService.getProcdef();
	}
	
}
