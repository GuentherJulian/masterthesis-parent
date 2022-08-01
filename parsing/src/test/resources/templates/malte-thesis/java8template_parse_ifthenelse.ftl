public class TestClass {
  <#if x == 1>
    private int xi = 2;
  <#elseif x == 2>
    private int xa = "xi";
  <#else>
    private String ya = "abc";
  </#if>
}