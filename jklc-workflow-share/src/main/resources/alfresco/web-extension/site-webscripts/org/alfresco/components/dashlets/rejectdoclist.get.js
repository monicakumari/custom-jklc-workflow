function main()
{
  
   var mySites = {
      id : "RejectDocuments", 
      name : "Alfresco.dashlet.RejectDocuments"
   };
   
   var dashletResizer = {
      id : "DashletResizer", 
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages : false
   };
   model.widgets = [mySites, dashletResizer];
}

main();