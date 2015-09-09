package com.dianping.pigeon.governor.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.pigeon.governor.bean.JqGridTableBean;
import com.dianping.pigeon.governor.bean.ServiceBean;
import com.dianping.pigeon.governor.bean.ServiceRetrieveBean;
import com.dianping.pigeon.governor.bean.ServiceRetrieveFilters;
import com.dianping.pigeon.governor.bean.WebResult;
import com.dianping.pigeon.governor.model.Service;
import com.dianping.pigeon.governor.service.PigeonConfigService;

@Controller
public class PigeonConfigController {
	
	private Logger log = LogManager.getLogger();
	
	@Autowired
	private PigeonConfigService pigeonConfigService;
	
	@RequestMapping(value = {"/services"}, method = RequestMethod.GET)
	public String allinone(ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response) {
		
		return "/services/index";
	}
	
	@RequestMapping(value = {"/services.api"}, method = RequestMethod.POST)
	public void servicesapi(ModelMap modelMap, ServiceBean serviceBean,
			HttpServletRequest request, HttpServletResponse response) {
		
		String oper = serviceBean.getOper();
		
		if("edit".equals(oper)){
			pigeonConfigService.updateById(serviceBean);
			
		}else if("del".equals(oper)){
			pigeonConfigService.deleteByIdSplitByComma(serviceBean.getId());
		
		}else if("add".equals(oper)){
			pigeonConfigService.create(serviceBean);
		
		}
		
	}
	
	@RequestMapping(value = {"/services.json"}, method = RequestMethod.GET)
	@ResponseBody
	public JqGridTableBean servicesjson(ModelMap modelMap, ServiceRetrieveBean serviceRetrieveBean,
			HttpServletRequest request, HttpServletResponse response) {
		
		ServiceRetrieveFilters filters = null;
		
		if(StringUtils.isNotBlank(serviceRetrieveBean.getFilters())){
			JSONObject jsonObj = JSONObject.fromObject(serviceRetrieveBean.getFilters());
			filters = (ServiceRetrieveFilters) JSONObject.toBean(jsonObj, ServiceRetrieveFilters.class);
			//log.info(JSONObject.fromObject(filters));
		}
		
		JqGridTableBean jqGridTableBean;
		
		int page = serviceRetrieveBean.getPage();
		int rows = serviceRetrieveBean.getRows();
		
		if(page > 0 && rows > 0){
			jqGridTableBean = pigeonConfigService.retrieveByJqGrid(page, rows);
		}else{
			jqGridTableBean = pigeonConfigService.retrieveByJqGrid(1, 10);
		}
		
		return jqGridTableBean;
	}
	
	/**
	 * 敏感操作权限控制
	 * @return
	 */
	public boolean authenticate(){
		
		return true;
	}
	
	
	
	
	
	//oldways
	
	@RequestMapping(value = {"/services.old"}, method = RequestMethod.GET)
	public String index(ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response) {
		
		List<Service> services = pigeonConfigService.retrieveAll();
		modelMap.addAttribute("services", services);
		
		return "/services/old";
	}

	@RequestMapping(value = {"/services/{id}"}, method = RequestMethod.GET)
	public String show(ModelMap modelMap, @PathVariable Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		modelMap.addAttribute("service", pigeonConfigService.retrieveById(id));
		
		return "/services/show";
	}
	
	@RequestMapping(value = {"/services/new"}, method = RequestMethod.GET)
	public String newPage(ModelMap modelMap,
			HttpServletRequest request, HttpServletResponse response) {
		
		return "/services/new";
	}
	
	@RequestMapping(value = {"/services/{id}/edit"}, method = RequestMethod.GET)
	public String edit(ModelMap modelMap, @PathVariable Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		modelMap.addAttribute("service", pigeonConfigService.retrieveById(id));
		
		return "/services/edit";
	}
	
	@RequestMapping(value = {"/services"}, method = RequestMethod.POST)
	@ResponseBody
	public WebResult create(ModelMap modelMap, ServiceBean serviceBean,
			HttpServletRequest request, HttpServletResponse response) {
		
		WebResult result = new WebResult(request);
		
		int sqlResult = pigeonConfigService.create(serviceBean);
		
		if(sqlResult > 0){
			result.setStatus(200);
			result.setMessage("Insert service successfully...");
		}else{
			result.setHasError(true);
			result.setErrorMsg("Failed to insert service...");
		}
		
		return result;
	}
	
	@RequestMapping(value = {"/services/{id}"}, method = {RequestMethod.POST, RequestMethod.PUT})
	@ResponseBody
	public WebResult update(ModelMap modelMap,
			ServiceBean serviceBean, @PathVariable Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		WebResult result = new WebResult(request);
		
		int sqlResult = pigeonConfigService.updateById(serviceBean);
		
		if(sqlResult > 0){
			result.setStatus(200);
			result.setMessage("Update service successfully...");
		}else{
			result.setHasError(true);
			result.setErrorMsg("Failed to update service...");
		}
		
		return result;
	}
	
	@RequestMapping(value = {"/services/{id}/delete"}, 
					method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
	@ResponseBody
	public WebResult destroy(ModelMap modelMap, @PathVariable Integer id,
			HttpServletRequest request, HttpServletResponse response) {
		
		WebResult result = new WebResult(request);
		
		// TODO 加入一些权限之类的判断条件
		int sqlResult = -1;
		if(authenticate()){
			sqlResult = pigeonConfigService.deleteById(id);
		}
		
		if(sqlResult > 0){
			result.setStatus(200);
			result.setMessage("Destroy service successfully...");
		}else{
			result.setStatus(500);
			result.setHasError(true);
			result.setErrorMsg("Failed to destroy service...");
		}
		
		return result;
	}
	
}
