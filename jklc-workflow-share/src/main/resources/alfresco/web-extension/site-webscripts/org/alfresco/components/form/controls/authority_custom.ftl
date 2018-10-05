<#include "common/picker_custom.inc.ftl" />
<#assign controlId = fieldHtmlId + "-cntrl">

<script type="text/javascript">//<![CDATA[
var userNodeRef="";
(function()
{
Alfresco.util.Ajax.jsonGet({
	url: Alfresco.constants.PROXY_URI + '/user/folderuser?nodeRef=${args.destination}',
	successCallback:{
		fn:function(response){	
			if(response.serverResponse.responseText.trim()!=""){
				userNodeRef=response.serverResponse.responseText;
			}
			callPicker(userNodeRef);
		},
		scope:this
	},
	failureCallback : {
	    fn : function(res) {
	    	console.log("ERROR :" +res);
	    	callPicker(userNodeRef);
	    },
	    scope : this
	  }
});
	
})();

function callPicker(userNodeRef){
   <@renderPickerJS field "picker" />
   console.log("userNodeRef:"+userNodeRef);
   picker.setOptions(
   {
      itemType: "${field.endpointType}",
      multipleSelectMode: true,
      itemFamily: "authority",
      itemType: "cm:person",
      selectedValue:userNodeRef.trim(),     
   });
}
//]]></script>

<div class="form-field">
   <#if form.mode == "view" >
      <div id="${controlId}" class="viewmode-field">
         <#if field.endpointMandatory && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
      </div>
   <#else>
      <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      
      <div id="${controlId}" class="object-finder">
      
         <div id="${controlId}-currentValueDisplay" class="current-values">
         </div>
         
         <#if field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
            <input type="hidden" id="${controlId}-added" name="${field.name}_added" />
            <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
            <div id="${controlId}-itemGroupActions" class="show-picker"></div>
            <@renderPickerHTML controlId />
         </#if>
         
      </div>
   </#if>
   
</div>




