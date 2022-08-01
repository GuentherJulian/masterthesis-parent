class A {

  void a() {
    <#if field.type=='boolean'>${"is" + fieldCapName}<#else>${OaspUtil.resolveIdGetter(field,false,"")}</#if>();
  }
  
}