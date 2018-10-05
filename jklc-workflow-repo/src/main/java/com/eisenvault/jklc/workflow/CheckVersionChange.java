package com.eisenvault.jklc.workflow;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.repo.version.VersionServicePolicies.AfterCreateVersionPolicy;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;


public class CheckVersionChange implements VersionServicePolicies.AfterCreateVersionPolicy {

	private PolicyComponent policyComponent;
	private ServiceRegistry serviceRegistry;
	private Logger logger = Logger.getLogger(CheckVersionChange.class);

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void init() {
		
		if (logger.isDebugEnabled())
			logger.debug("\n ******************* Initializing CheckVersionChange JKLC workflow behavior ******************* \n");
		
		this.policyComponent.bindClassBehaviour(AfterCreateVersionPolicy.QNAME, ContentModel.PROP_CONTENT,
				new JavaBehaviour(this, "afterCreateVersion", NotificationFrequency.EVERY_EVENT));
	}

	@Override
	public void afterCreateVersion(final NodeRef currentNode, final Version version) {
		try {
			final NodeService nodeService = serviceRegistry.getNodeService();
			final String [] ocrDesc = new String[2];
			ocrDesc[0] = "OCR run on this document";
			ocrDesc[1] = "OCR run on this document and converted to PDF";
			logger.debug("The description of the new version is: " + version.getDescription());
			
			if (!"1.0".equals(version.getVersionLabel()) || nodeService.hasAspect(currentNode, WorkflowUserActionExecuter.ASPECT_OCRED_PDF_FROM_IMAGE)) {
				ChildAssociationRef parentNodeRef = nodeService.getPrimaryParent(currentNode);
				final NodeRef parent = parentNodeRef.getParentRef();
				if (nodeService.hasAspect(parent, WorkflowUserActionExecuter.ASPECT_USER)) {

					AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
						public Object doWork() throws Exception {
							
							if(Arrays.asList(ocrDesc).contains(version.getDescription()) || nodeService.hasAspect(currentNode, WorkflowUserActionExecuter.ASPECT_OCRED_PDF_FROM_IMAGE)){
								logger.debug("Turns out the version is because of OCR. Bailing!!!, Abort!!!, Mayday!!!");
								return "";
							}
							
							if (nodeService.hasAspect(currentNode,
									WorkflowUserActionExecuter.ASPECT_APPROVED_DOCUMENT)) {
								nodeService.removeAspect(currentNode,
										WorkflowUserActionExecuter.ASPECT_APPROVED_DOCUMENT);
							}
							if (nodeService.hasAspect(currentNode,
									WorkflowUserActionExecuter.ASPECT_REJECTED_DOCUMENT)) {
								nodeService.removeAspect(currentNode,
										WorkflowUserActionExecuter.ASPECT_REJECTED_DOCUMENT);
							}
							changeVersion(version, nodeService, parent, currentNode);
							return "";
						}

					});
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void changeVersion(Version version, NodeService nodeService, NodeRef parent, NodeRef currentNode) {

		logger.debug("Version Change");

		Object ruleNodeRef = nodeService.getProperty(parent,
				QName.createQName(WorkflowUserActionExecuter.CUSTOM_MODEL_URI, "ruleNodeRef"));

		boolean isExecuteRule = false;
		if (ruleNodeRef != null && !"".equals(ruleNodeRef) && nodeService.exists(new NodeRef(ruleNodeRef.toString()))) {
			@SuppressWarnings("deprecation")
			List<Rule> rules = serviceRegistry.getRuleService().getRules(parent);
			for (Rule rule : rules) {

				if (!rule.getRuleDisabled() && rule.getNodeRef().toString().equals(ruleNodeRef)) {
					isExecuteRule = true;
					break;
				}
				
			}
		}
		logger.debug("isExcuteRule:" + isExecuteRule);
		if (isExecuteRule) {
			serviceRegistry.getPermissionService().setPermission(currentNode,
					serviceRegistry.getAuthenticationService().getCurrentUserName(), PermissionService.CONTRIBUTOR,
					true);
			
			// TODO: Need to find how to execute rule applied on folder programatically
			
			NodeRef currentUser=serviceRegistry.getPersonService().getPerson(serviceRegistry.getAuthenticationService().getCurrentUserName());
			
			String workFlowDescription = "Document  \"" + nodeService.getProperty(currentNode, ContentModel.PROP_NAME)
					+ "\" has been submitted for your approval by " + nodeService.getProperty(currentUser, ContentModel.PROP_FIRSTNAME) + " "
					+ nodeService.getProperty(currentUser, ContentModel.PROP_LASTNAME);
			WorkflowService workflowService = serviceRegistry.getWorkflowService();
			NodeRef workflowPackage = workflowService.createPackage(null);
			String wid = workflowService.getDefinitionByName("activiti$docApproveReject").getId();
			Map<QName, Serializable> parameters = new HashMap<QName, Serializable>();
			parameters.put(WorkflowModel.ASSOC_PACKAGE, workflowPackage);
			parameters.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, workFlowDescription);
			nodeService.addChild(workflowPackage, currentNode, ContentModel.ASSOC_CONTAINS,
					QName.createQName("packageItems"));
			workflowService.startWorkflow(wid, parameters);

			/*if(nodeService.hasAspect(currentNode, WorkflowUserActionExecuter.OCR_ASPECT)) {
				nodeService.removeAspect(currentNode, WorkflowUserActionExecuter.OCR_ASPECT);
				logger.debug("insde if----has-- aspect");
			} else {
				workflowService.startWorkflow(wid, parameters);
				logger.debug("insde else  ----No-- aspect");
			}*/			
		}	
	}
}
