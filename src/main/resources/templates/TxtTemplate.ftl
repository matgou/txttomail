<#import 'utils.ftl' as u>

<#if (previusTag)?? && previusTag != "TEXT">
<p>
</#if>
<#if !(previusTag)??>
<p>
</#if>
${u.handleLink(line)}<br/>
<#if (nextTag)?? && nextTag != "TEXT">
</p>
</#if>
<#if !(nextTag)??> 
</p>
</#if>
