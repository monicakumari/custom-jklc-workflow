function main() {
	var results = search.luceneSearch("PATH:\"/app:company_home/st:sites//* \" AND  +ASPECT:\"scwf:rejectDocument\"  AND  +@cm\\:owner:\""+ person.properties.userName +"\"");
	model.ans=results;
	/*for(i = 0; i < results.length; i++){
	    var node = results[i];
	    logger.log(node.getNodeRef());
	}*/
	
}
main();

