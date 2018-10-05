package com.eisenvault.jklc.workflow;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.evaluator.NoConditionEvaluator;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.action.executer.ScriptActionExecuter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.action.CompositeAction;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

/**
 * 
 * @author Sanjay Bandhniya
 * 
 *         c2 This class add aspect to folder with usernames
 */

public class WorkflowUserActionExecuter extends ActionExecuterAbstractBase {

	private Logger logger = Logger.getLogger(WorkflowUserActionExecuter.class);
	
	public static final String CUSTOM_MODEL_URI = "http://www.jkl.com/model/workflow/1.0";
	public static final String EV_MODEL_URI = "http://www.eisenvault.net/model/content/1.0";
	public static final QName OCR_ASPECT = QName.createQName(EV_MODEL_URI,"remoteOCR");
	public static final QName ASPECT_OCRED_PDF_FROM_IMAGE = QName.createQName(EV_MODEL_URI, "ocrPdfFromImages");
	public static final String SELECTED_USER = "selectedUser";
	public static final String APPLY_TO_SUBFOLDERS = "applySubfolders";
	public static final QName ASPECT_USER = QName.createQName(CUSTOM_MODEL_URI, "userSelect");
	public static final QName ASPECT_APPROVED_DOCUMENT = QName.createQName(CUSTOM_MODEL_URI, "approveDocument");
	public static final QName ASPECT_REJECTED_DOCUMENT= QName.createQName(CUSTOM_MODEL_URI, "rejectDocument");

	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		paramList.add(new ParameterDefinitionImpl(SELECTED_USER, DataTypeDefinition.NODE_REF, true,
				getParamDisplayLabel(SELECTED_USER)));
		paramList.add(new ParameterDefinitionImpl(APPLY_TO_SUBFOLDERS, DataTypeDefinition.BOOLEAN, true,
				getParamDisplayLabel(APPLY_TO_SUBFOLDERS)));
	}

	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		try {
			NodeService nodeService = serviceRegistry.getNodeService();

			String userNameNodes = (String) action.getParameterValue(SELECTED_USER).toString();
			Boolean applySubfolders = (Boolean) action.getParameterValue(APPLY_TO_SUBFOLDERS);
			String[] users = userNameNodes.split(",");
			StringBuffer userNames = new StringBuffer();
			for (String user : users) {
				String userName = nodeService.getProperty(new NodeRef(user), ContentModel.PROP_USERNAME).toString();
				userNames.append(userName).append(",");
			}
			userNames = userNames.deleteCharAt(userNames.length() - 1);
			// add aspect to folder
			Map<QName, Serializable> aspectValues = new HashMap<QName, Serializable>();
			
			QName USER_NAME = QName.createQName(CUSTOM_MODEL_URI, "userName");
			aspectValues.put(USER_NAME, userNames.toString());
			serviceRegistry.getNodeService().addAspect(actionedUponNodeRef, ASPECT_USER, aspectValues);

			logger.debug("Has Aspect :" + nodeService.hasAspect(actionedUponNodeRef, ASPECT_USER));

			if (nodeService.hasAspect(actionedUponNodeRef, ASPECT_USER)) {
				createRule(actionedUponNodeRef, nodeService, applySubfolders);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createRule(NodeRef actionedUponNodeRef, NodeService nodeService, Boolean apply) {
		@SuppressWarnings("deprecation")
		RuleService ruleService = serviceRegistry.getRuleService();
		ActionService actionService = serviceRegistry.getActionService();

		Object ruleNodeRef = nodeService.getProperty(actionedUponNodeRef,QName.createQName(CUSTOM_MODEL_URI, "ruleNodeRef"));

		logger.debug(ruleNodeRef);

		boolean isApplyRule = true;
		if (ruleNodeRef != null && !"".equals(ruleNodeRef) && nodeService.exists(new NodeRef(ruleNodeRef.toString()))) {
			List<Rule> rules = ruleService.getRules(actionedUponNodeRef);
			for (Rule rule : rules) {
				if (rule.getNodeRef().toString().equals(ruleNodeRef)) {
					isApplyRule = false;
					break;
				}

			}
		}

		if (isApplyRule) {

			SearchService searchService = serviceRegistry.getSearchService();
			ResultSet rs = searchService.query(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore"),
					SearchService.LANGUAGE_LUCENE,
					"PATH:\"/app:company_home/app:dictionary/app:scripts/cm:eisenvault-dynamic-workflow.js\"");

			if (rs != null && rs.length() > 0) {
				Rule rule = new Rule();
				rule.setRuleType(RuleType.INBOUND);
				if(apply)
					rule.applyToChildren(true);
				else
					rule.applyToChildren(false);
				rule.setTitle("Start Workflow On Document Upload");
				rule.setDescription("When Document Enter in folder it will start workflow");

				NodeRef scriptRef = new NodeRef(rs.getNodeRef(0).toString());
				CompositeAction compositeAction = actionService.createCompositeAction();
				ActionCondition actionCondition = actionService.createActionCondition(NoConditionEvaluator.NAME);
				compositeAction.addActionCondition(actionCondition);
				compositeAction.setTitle("Start_Workflow");
				Action action = actionService.createAction(ScriptActionExecuter.NAME);
				action.setExecuteAsynchronously(false);
				action.setTitle("Start Workflow");
				action.setParameterValue(ScriptActionExecuter.PARAM_SCRIPTREF, scriptRef);
				compositeAction.addAction(action);
				rule.setAction(compositeAction);
				ruleService.saveRule(actionedUponNodeRef, rule);

				Map<QName, Serializable> aspectValues = new HashMap<QName, Serializable>();
				QName ASPECT_USER = QName.createQName(CUSTOM_MODEL_URI, "userSelect");
				QName USER_NAME = QName.createQName(CUSTOM_MODEL_URI, "ruleNodeRef");
				aspectValues.put(USER_NAME, rule.getNodeRef().toString());
				serviceRegistry.getNodeService().addAspect(actionedUponNodeRef, ASPECT_USER, aspectValues);

			}

		}
	}
}