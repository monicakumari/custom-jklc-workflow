 
 
 [
<#list ans as node>
{
"name" : "${node.properties.name}",
"nodeRef" : "${node.nodeRef}"
}<#if node_has_next>,</#if> 
</#list>
]