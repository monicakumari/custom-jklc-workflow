(function(_submitInvoked){var redirectCallback;_queryParam=function(key){var vars=[],hash;var hashes=window.location.href.slice(window.location.href.indexOf("?")+1).split("&");for(var i=0;i<hashes.length;i++){hash=hashes[i].split("=");vars.push(hash[0]);vars[hash[0]]=hash[1]}return vars[key]};var _workflowDetailsSuccessCallback=function(response,obj){var queryParamRedir=_queryParam("redirect");if(queryParamRedir==null||queryParamRedir.length==0){queryParamRedir=encodeURIComponent(document.referrer)}if(response.json.data.length>0){var task=response.json.data[0];var taskId=task.id;if(Alfresco.constants.USERNAME==task.owner.userName){var packageNodeRef=task.workflowInstance["package"];Alfresco.util.Ajax.jsonGet({url:Alfresco.constants.PROXY_URI+"slingshot/node/"+packageNodeRef.replace("://","/"),successCallback:{fn:function(res){var result=eval("("+res.serverResponse.responseText+")");redirectCallback.scope.options.submitUrl=Alfresco.constants.URL_PAGECONTEXT+"task-edit?taskId="+taskId;if(result.children.length>0){var docNodeRef=result.children[0].nodeRef;redirectCallback.scope.options.submitUrl=Alfresco.constants.URL_PAGECONTEXT+"task-edit?taskId="+taskId+"&nodeRef="+docNodeRef}redirectCallback.fn.call(redirectCallback.scope,obj.response)},scope:this},failureCallback:{fn:function(res){console.log("ERROR :"+res)},scope:this}})}else{if(queryParamRedir!=null){redirectCallback.scope.options.submitUrl=decodeURIComponent(queryParamRedir);redirectCallback.fn.call(redirectCallback.scope,obj.response)}}}else{if(queryParamRedir!=null){redirectCallback.scope.options.submitUrl=decodeURIComponent(queryParamRedir);redirectCallback.fn.call(redirectCallback.scope,obj.response)}}};var _newSuccessCallback=function(response){var persistedObject=response.json.persistedObject;persistedObject=persistedObject.substr(persistedObject.indexOf("WorkflowInstance"));var startIndex=persistedObject.indexOf("id=activiti$")+3;var endIndex=persistedObject.indexOf(",",17);var activitiId=persistedObject.substr(startIndex,endIndex-startIndex);Alfresco.util.Ajax.request({url:Alfresco.constants.PROXY_URI+"api/task-instances?authority="+Alfresco.constants.USERNAME,successCallback:{fn:_workflowDetailsSuccessCallback,scope:this,obj:{response:response,activitiId:activitiId}},failureMessage:Alfresco.util.message("message.failure"),scope:this,execScripts:false})};Alfresco.forms.Form.prototype._submitInvoked=function(event){if(redirectCallback===undefined||redirectCallback===null){redirectCallback=this.ajaxSubmitHandlers.successCallback}this.ajaxSubmitHandlers.successCallback={fn:_newSuccessCallback,obj:null,scope:this};_submitInvoked.call(this,event)}}(Alfresco.forms.Form.prototype._submitInvoked));