<#import 'utils.ftl' as u>
<#assign TYPE_COLOR="rgb(0, 0, 153);"> 
<#assign aDateTime = .now>
<#if TYPE =="INFO">
  <#assign TYPE_COLOR="rgb(0, 0, 153);"> 
</#if> 
<#if TYPE =="ERROR">
  <#assign TYPE_COLOR="rgb(250, 0, 0);"> 
</#if>
<div style="background-color:#f2ffe6;">
  <table height="100%" width="100%" cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td valign="top" align="left" background="">
<table height="100%" width="90%" cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td valign="top" align="left" background="#6aa517">
<table style="background-color:#6aa517; text-align: left;"
 border="0" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td style="width: 176px;"><img style="width: 70px; height: 70px;" src="${u.image('Logo.png')}"></td>
      <td style="width: 100%; margin-left: 20px;">
      <table class="MsoNormalTable" style="border: medium none ; border-collapse: collapse; width: 100%;" border="0" cellpadding="0" cellspacing="0">
        <tbody>
          <tr style="height: 90.95pt;">
            <td style="border: medium none ; padding: 0cm; width: 377.15pt; height: 60pt;">
            <p class="MsoNormal" style="margin-bottom: 3pt;"><b><span style="font-size: 20pt; font-family: &quot;Calibri&quot;,sans-serif;"><span
 style="color: ${TYPE_COLOR}">${TYPE} :</span><span style="color: white;"> ${SUBJECT}</span>
 				
 			<o:p></o:p></span></b></p>
            <p class="MsoNormal" style="margin-bottom: 3pt;"><b style=""><span style="font-family: &quot;Calibri&quot;,sans-serif; color: white;"><span style="color: black;">Date : </span></span></b><span style="color: white;">
            	${aDateTime?string["dd/MM/yyyy, HH:mm"]}
            </span></p>            </td>
          </tr>
        </tbody>
      </table>
      </td>
    </tr>
  </tbody>
</table>
</td></tr><tr style="background-color: white;">
<td valign="top" align="left" background="#ffffff"><br/>
