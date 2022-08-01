import a.c.b;

public class TestClass{

  ${mod} int i;

  <#if x == 1>
    private int i1;
  <#elseif x == 2>
    private int i2;
  <#else>
    private int ix;
  </#if>

  /*<#list fieldnames as fieldname>
    private int ${fieldname};
  </#list>*/

}