package com.eisenvault.jklc.workflow.evaluators;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.User;


public class ShowActionEvaluators extends BaseEvaluator {
	
	
	@Override
	public boolean evaluate(JSONObject jsonObject) {
		// TODO Auto-generated method stub
	//	RequestContext rc = ThreadLocalRequestContext.getRequestContext();
	//	User user = rc.getUser();
	//	System.out.println("---------"+ jsonObject.toJSONString());
		return true;
		//return (user != null && user.);
		
		
	}
	 
}
