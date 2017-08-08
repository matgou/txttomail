<#import 'utils.ftl' as u>

<#assign array=u.loadCSVFile(line)>
<table style="border-collapse: collapse;    border-spacing: 0; ">
<#list array as row>
	<#if row?counter == 1><tr style="background-color:#7FA52F;color:white;"><#else><tr></#if>
	<#list row as cell>
	   	<td style="border: medium none; padding: 8px; text-align: left;">${cell}</td>
	</#list>
    </tr>
</#list> 
</table>
<br/>