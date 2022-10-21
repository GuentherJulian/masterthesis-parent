<#if anything>
class A {
    <#list field as fields>
    ${field}
    </#list>
}
<#else>
class B {
    
}
</#if>