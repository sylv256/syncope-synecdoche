<#assign impl>io.github.rehtea.syncope.impl</#assign>
<#assign implClient>io.github.rehtea.syncope.client.impl</#assign>
<#if packageName?starts_with(impl) || packageName?starts_with(implClient)>
	<#assign isImpl>true</#assign>
<#else>
<#assign isImpl>false</#assign>
</#if>
<#if isImpl?boolean>
@ApiStatus.Internal
</#if>
@NullMarked
package ${packageName};

<#if isImpl?boolean>
import org.jetbrains.annotations.ApiStatus;
</#if>
import org.jspecify.annotations.NullMarked;
