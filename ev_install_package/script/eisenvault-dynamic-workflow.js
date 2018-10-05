if(document.isDocument){

var nodeObject=document.getParent();
if(nodeObject.hasAspect("scwf:userSelect")){
	var userNames= nodeObject.properties["scwf:userName"];
	if(userNames!=""){
		var workflow = actions.create("start-workflow");
		workflow.parameters.workflowName = "activiti$docApproveReject";
		workflow.parameters["bpm:workflowDescription"] = "Document  \"" + document.name + "\" has been submitted for your approval by "+ person.properties.firstName + " " +person.properties.lastName;
		workflow.execute(document);
	}
}

}