public class TestClass {
  
  <#if x == 1>
    private int xi;
  <#elseif x == 2>
    private int xa;
  <#else>
    private String ya;
  </#if>
}