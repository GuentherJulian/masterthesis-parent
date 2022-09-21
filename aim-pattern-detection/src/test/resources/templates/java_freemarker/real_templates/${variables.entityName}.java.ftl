<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;
<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>

<#if compositeIdTypeVar=="null">
public interface ${variables.entityName} extends ApplicationEntity {

	<@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion false true/>

}
<#else>
public interface ${variables.entityName} {

	<@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion false true/>

}
</#if>

