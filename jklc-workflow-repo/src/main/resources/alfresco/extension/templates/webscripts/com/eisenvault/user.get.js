var node = args["nodeRef"];

var nodeObject = search.findNode(node);
var userNodeRef="";
if(nodeObject.hasAspect("scwf:userSelect")){
	var userNames= nodeObject.properties["scwf:userName"];
	var users=userNames.split(",");
	for(i in users){
		var person=groups.getUser(users[i]);
		userNodeRef+=person.personNodeRef+",";	
	}
	userNodeRef=userNodeRef.substring(0, userNodeRef.length - 1)
}

model.node = userNodeRef.trim();
