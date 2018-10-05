<#include "include/alfresco-template.ftl" />

<@templateHeader>
<#assign dependencyGroup="web-preview">
<#include "include/web-preview-css-dependencies.lib.ftl" />
<#include "include/web-preview-js-dependencies.lib.ftl" />


<@script src="${url.context}/res/components/documentlibrary/actions.js"/>
<@script src="${url.context}/res/components/document-details/document-versions.js" group="document-details"/>
<@script src="${url.context}/res/modules/document-details/revert-version.js" group="document-details"/>
<@script src="${url.context}/res/modules/document-details/historic-properties-viewer.js" group="document-details"/>
<@script src="${url.context}/res/components/document-details/document-metadata.js" group="document-details"/>
<@script src="${url.context}/res/components/document-details/document-links.js" group="document-details"/>
<@script src="${url.context}/res/modules/documentlibrary/doclib-actions.js" group="document-details"/>
<@script src="${url.context}/res/components/document-details/document-actions.js" group="document-details" />
<@script src="${url.context}/res/components/comments/comments-list.js" group="comments"/>
<@script src="${url.context}/res/modules/simple-dialog.js"/>
<@script src="${url.context}/res/modules/documentlibrary/aspects.js"/>
<@script src="${url.context}/res/modules/documentlibrary/global-folder.js"/>
<@script src="${url.context}/res/modules/documentlibrary/copy-move-to.js"/>
<@script src="${url.context}/res/components/upload/file-upload.js" group="upload"/>
<@script src="${url.context}/res/components/upload/html-upload.js" group="upload"/>
<@script src="${url.context}/res/components/upload/flash-upload.js" group="upload"/>
<@script src="${url.context}/res/components/upload/dnd-upload.js" group="upload"/>



<@link href="${url.context}/res/components/document-details/document-details-panel.css" group="document-details" />
<@link href="${url.context}/res/components/upload/html-upload.css" group="upload" />
<@link href="${url.context}/res/components/upload/flash-upload.css" group="upload"/>
<@link href="${url.context}/res/components/upload/dnd-upload.css" group="upload"/>
<@link href="${url.context}/res/components/documentlibrary/actions.css"/>
<@link href="${url.context}/res/modules/documentlibrary/aspects.css"/>
<@link href="${url.context}/res/modules/documentlibrary/global-folder.css"/>
<@link href="${url.context}/res/modules/documentlibrary/copy-to.css"/>
<@link href="${url.context}/res/components/documentlibrary/toolbar.css"/>
<@link href="${url.context}/res/components/document-details/document-actions.css" group="document-details" />
<@link href="${url.context}/res/components/comments/comments-list.css" group="comments"/>
<@link href="${url.context}/res/components/document-details/document-metadata.css" group="document-details"/>
</@>


<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
      <@region id="title" scope="template"/>
      <#if page.url.args.nodeRef??>
         <@region id="path" scope="template"/>
      </#if>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      
      <div class="share-form">
         <@region id="data-header" scope="page" />
         <@region id="data-form" scope="page" />
         <@region id="data-actions" scope="page" />
        </div>  
      
      <#if page.url.args.nodeRef??>
      <br>
      <hr/>
      <br>
      <div>
       <div class="yui-gc">
         <div class="yui-u first">
               <@region id="metadata-web-preview" scope="template" />
               <@region id="metadata-comments" scope="template"/>
         </div>
         <div class="yui-u">
         	<@region id="metadata-document-actions" scope="template" />
            <@region id="metadata-document-links" scope="template" />
            <@region id="metadata-document-metadata" scope="template" />
            <@region id="metadata-document-workflow" scope="template" />
      	   <@region id="metadata-document-versions" scope="template" /> 
         </div>
      </div>
       <@region id="metadata-html-upload" scope="template"/>
       <@region id="metadata-flash-upload" scope="template"/> 
       <@region id="metadata-file-upload" scope="template"/> 
       <@region id="metadata-dnd-upload" scope="template"/>  
       </div>
     </#if>

          
     
   </div>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global"/>
      <@region id="data-loader" scope="page" />
   </div>
   </@>
</@>
