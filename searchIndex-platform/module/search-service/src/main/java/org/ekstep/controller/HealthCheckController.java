package org.ekstep.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ekstep.searchindex.elasticsearch.ElasticSearchUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ilimi.common.controller.BaseController;
import com.ilimi.common.dto.Response;
import com.ilimi.common.dto.ResponseParams;
import com.ilimi.common.dto.ResponseParams.StatusType;
import com.ilimi.common.exception.ResponseCode;

@Controller
@RequestMapping("health")
public class HealthCheckController extends BaseController {

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> search() {
		String name = "search-service";
		String apiId = name + ".health";
		ElasticSearchUtil es = new ElasticSearchUtil();
		Response response = new Response();
		ResponseParams params = new ResponseParams();
		List<Map<String, Object>> checks = new ArrayList<Map<String, Object>>();
		Map<String, Object> esCheck = new HashMap<String, Object>();
		esCheck.put("name", "ElasticSearch");
		boolean index = false;
		try {
			index = es.isIndexExists("compositesearch");

			response.put("name", name);
			
			if (index == true) {
				params.setErr("0");
				params.setStatus(StatusType.successful.name());
				params.setErrmsg("Operation successful");
				response.setParams(params);
				response.put("healthy", true);
				esCheck.put("healthy", true);
			} else{
				params.setErrmsg("Elastic Search index is not set");
				params.setStatus(StatusType.failed.name());
				response.setResponseCode(ResponseCode.SERVER_ERROR);
				response.setParams(params);
				response.put("healthy", false);
				esCheck.put("healthy", false);
	    		esCheck.put("err", ""); // error code, if any
	    		esCheck.put("errmsg", "Elastic Search index is not set"); // default English error message 

			}
			checks.add(esCheck);
			response.put("checks", checks);
		} catch (Exception e) {
				ResponseParams resStatus = new ResponseParams();
				resStatus.setErrmsg("SERVER_ERROR");
				resStatus.setStatus(StatusType.failed.name());
				response.setResponseCode(ResponseCode.SERVER_ERROR);
				response.setParams(resStatus);
				response.put("healthy", false);
				esCheck.put("healthy", false);
	    		esCheck.put("err", "503"); // error code, if any
	    		esCheck.put("errmsg", e.getMessage()); // default English error message 

				checks.add(esCheck);
				response.put("checks", checks);
				return getResponseEntity(response, apiId, null);
		}
		return getResponseEntity(response, apiId, null);
	}
		
}
