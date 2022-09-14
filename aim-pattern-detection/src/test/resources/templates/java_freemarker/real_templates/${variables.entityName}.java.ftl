<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;
<#assign compositeIdTypeVar = JavaUtil.getReturnTypeOfMethodAnnotatedWith(classObject,"javax.persistence.EmbeddedId")>

public interface ${variables.entityName} extends ApplicationEntity {

	<@generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion false true/>

}
