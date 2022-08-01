
public class ${variables.entityName?cap_first + "RepoImpl"} implements ${variables.entityName?cap_first + "RepoCustom"} {

  public PaginatedListTo ${"find" + variables.entityName + "s"}(${variables.entityName + "SearchCriteriaTo"} criteria) {

    JPAQuery query = new JPAQuery(getEntityManager()).from(${"getQ" + variables.entityName + "Entity"}());
    
    <#list pojo.fields as field>
      JPAQuery query = new JPAQuery(getEntityManager()).from(${"getQ" + variables.entityName + "Entity"}());
    </#list>
    
  }
  
}