
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Selector = YAHOO.util.Selector;

   var $html = Alfresco.util.encodeHTML,
   $siteURL = Alfresco.util.siteURL;
   
   Alfresco.dashlet.RejectDocuments = function RejectDocuments_constructor(htmlId)
   {
      Alfresco.dashlet.RejectDocuments.superclass.constructor.call(this, "Alfresco.dashlet.RejectDocuments", htmlId, ["datasource", "datatable", "animation"]);

      this.docs = [];

      return this;
   };

   YAHOO.extend(Alfresco.dashlet.RejectDocuments, Alfresco.component.Base,
   {
      
      docs: null,
      onReady: function RejectDocuments_onReady()
      {
        
         // DataSource definition
         this.widgets.dataSource = new YAHOO.util.DataSource(this.docs,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "icon", label: "Icon", sortable: false, formatter: this.bind(this.renderCellIcon), width: 52 },
            { key: "detail", label: "Description", sortable: false, formatter: this.bind(this.renderCellDetail) },
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-docs", columnDefinitions, this.widgets.dataSource,
         {
            MSG_EMPTY: this.msg("message.datatable.loading")
         });
         this.loadDocuments();
      },

   
    
      loadDocuments: function RejectDocuments_loadDocuments()
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "jklc/rejecteddocument",
            successCallback:
            {
               fn: this.onDocumenLoaded,
               scope: this
            }
         });
      },

     
      onDocumenLoaded: function RejectDocuments_onDocumenLoaded(p_response)
      {
         var p_items = p_response.json,
             i, j,ii = 0;

         this.docs = [];
         for (i = 0, j = p_items.length; i < j; i++)
         {
            var record = YAHOO.lang.merge({}, p_items[i]);

               this.docs[ii] = record;
               ii++;
         }

         var successHandler = function RejectDocuments_onSitesUpdate_success(sRequest, oResponse, oPayload)
         {
            oResponse.results=this.docs;
            this.widgets.dataTable.set("MSG_EMPTY", "");
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };

         this.widgets.dataSource.sendRequest(this.docs,
         {
            success: successHandler,
            scope: this
         });
      },
     
      renderCellIcon: function RejectDocuments_renderCellIcon(elCell, oRecord, oColumn, oData)
      {
         Dom.setStyle(elCell, "width", oColumn.width + "px");
         Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
         elCell.innerHTML = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/document-view-details-16.png" />';
      },

      renderCellDetail: function RejectDocuments_renderCellDetail(elCell, oRecord, oColumn, oData)
      {
    	  
         var doc = oRecord.getData(),desc = "";
         var link =  $siteURL("document-details?nodeRef=" + doc.nodeRef)
            desc += '<div class="empty"><h3><a href='+link+'>'+doc.name+'</a></h3>';

         elCell.innerHTML = desc;
      }
   });
})();