

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/reject-documents.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign id = args.htmlid?html>
      <div class="dashlet my-sites">
         <div class="title">Rejected Documents</div>
        
         <div id="${id}-docs" class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>></div>
      </div>
   </@>
</@>