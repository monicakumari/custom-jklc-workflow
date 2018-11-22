package com.eisenvault.jklc.workflow;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

public class ApplyAspectOnCreate implements NodeServicePolicies.OnCreateNodePolicy{

	private Behaviour onCreateNode;
	private PolicyComponent policyComponent;
	
	private NodeService nodeService;
	
	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void init() {		
		// Create behaviours
		this.onCreateNode = new JavaBehaviour(this, "onCreateNode",	NotificationFrequency.TRANSACTION_COMMIT);
		// Bind behaviours to node policies
		this.policyComponent.bindClassBehaviour(QName.createQName(
				NamespaceService.ALFRESCO_URI, "onCreateNode"), ContentModel.TYPE_FOLDER, this.onCreateNode);
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		
		NodeRef nodeRef = childAssocRef.getChildRef();
		NodeRef parentNode = childAssocRef.getParentRef();
		
		QName USER_NAME = QName.createQName(WorkflowUserActionExecuter.CUSTOM_MODEL_URI, "userName");
	
		AuthenticationUtil.runAsSystem(
			       new AuthenticationUtil.RunAsWork<Object>() {

			           public Object doWork() throws Exception {
						   if(nodeService.exists(nodeRef)){
							   if(nodeService.hasAspect(parentNode, WorkflowUserActionExecuter.ASPECT_USER)){
								   nodeService.setProperty(nodeRef, USER_NAME, nodeService.getProperty(parentNode, USER_NAME));
							   }
						   }
			               return null;
			           }
			       });
	}
}
