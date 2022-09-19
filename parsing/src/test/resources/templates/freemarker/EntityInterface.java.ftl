package ${variables.rootPackage}.${variables.component}.common.api;

import ${variables.rootPackage}.general.common.api.ApplicationEntity;


public interface ${variables.entityName} extends ApplicationEntity {

<#list pojo.fields as field>
<#if field.type?contains("Entity")> 
   <#if !JavaUtil.isCollection(classObject, field.name)> 

    	<#if !implementsInterface>
      /**
      * getter for ${field.name + 'Id'} attribute
      * @return ${field.name + 'Id'}
      */
      </#if>
    	<#if implementsInterface>@Override</#if>
    	public ${DevonfwUtil.getSimpleEntityTypeAsLongReference(field)} ${DevonfwUtil.resolveIdGetter(field,false,"")} <#if isInterface>;<#else>{
    		return ${idVar};
    	}</#if>
      <#if !implementsInterface>
      /**
      * @param ${field.name}
      *            setter for ${field.name} attribute
      */
      </#if>      
    	<#if implementsInterface>@Override</#if>
    	public void ${DevonfwUtil.resolveIdSetter(field,false,"")}(${DevonfwUtil.getSimpleEntityTypeAsLongReference(field)} ${idVar}) <#if isInterface>;<#else>{
    		this.${idVar} = ${idVar};
    	}</#if>
   </#if>
<#elseif field.type?contains("Embeddable")>
	<#if isSearchCriteria>
	    /**		 
		 * @return ${field.name}
		 */
		public ${field.type?replace("Embeddable","SearchCriteriaTo")} ${'g' + 'e' + 't' + field.name?cap_first}() <#if isInterface>;<#else>{
			return ${field.name};
		}</#if>

		/**
		 * @param ${field.name}
		 *            setter for ${field.name} attribute
		 */
		public void ${'s' + 'e' + 't' + field.name?cap_first}(${field.type?replace("Embeddable","SearchCriteriaTo")} ${field.name}) <#if isInterface>;<#else>{
			this.${field.name} = ${field.name};
		}</#if>
	<#else>
		/**
		 * @return ${field.name + 'Id'}
		 */
		public ${field.type?replace("Embeddable","")} <#if field.type=='boolean'>is<#else>get</#if>${field.name?cap_first}() <#if isInterface>;<#else>{
			return ${field.name};
		}</#if>

		/**
		 * @param ${field.name}
		 *            setter for ${field.name} attribute
		 */
		public void ${'s' + 'e' + 't' + field.name?cap_first}(${field.type?replace("Embeddable","")} ${field.name}) <#if isInterface>;<#else>{
			this.${field.name} = ${field.name};
		}</#if>
	</#if>
<#elseif !isSearchCriteria || !JavaUtil.isCollection(classObject, field.name)>
  <#if !implementsInterface>
      /**
      * @return ${field.name + 'Id'}
      */
  </#if>
  <#if implementsInterface>@Override</#if>
	public <#if isSearchCriteria>${JavaUtil.boxJavaPrimitives(classObject,field.name)} ${'g' + 'e' + 't' + field.name?cap_first}() <#else>${field.type} <#if field.type=='boolean'>is<#else>get</#if>${field.name?cap_first}()</#if> <#if isInterface>;<#else>{
		return ${field.name};
	}</#if>
  <#if !implementsInterface>
  /**
   * @param ${field.name}
   *            setter for ${field.name} attribute
   */
  </#if>
	<#if implementsInterface>@Override</#if>
	public void ${'s' + 'e' + 't' + field.name?cap_first}(<#if isSearchCriteria>${JavaUtil.boxJavaPrimitives(classObject,field.name)}<#else>${field.type}</#if> ${field.name}) <#if isInterface>;<#else>{
		this.${field.name} = ${field.name};
	}</#if>
</#if>
</#list>
<#if isSearchCriteria>
	<#list pojo.fields as field>
		<#if field.type="String">
	/**
	* @return the {@link StringSearchConfigTo} used to search for {@link #${'g' + 'e' + 't' + field.name?cap_first}<#if field.type?contains("Entity")>Entity</#if>() ${field.name}}.
	*/
	public StringSearchConfigTo ${'g' + 'e' + 't' + field.name?cap_first + 'Option'}() {

		return this.${field.name + 'Option'};
	}

	/**
	* @param ${field.name + 'Option'} new value of {@link #${'g' + 'e' + 't' + field.name?cap_first + 'Option'}()}.
	*/
	public void ${'s' + 'e' + 't' + field.name?cap_first + 'Option'}(StringSearchConfigTo ${field.name + 'Option'}) {

		this.${field.name + 'Option'} =${field.name + 'Option'};
	}
		</#if>
	</#list>
</#if>

}