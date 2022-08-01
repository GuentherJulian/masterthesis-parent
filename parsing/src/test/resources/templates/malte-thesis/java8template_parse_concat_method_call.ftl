class A {

  void a() {
    query.where(${"getQ" + variables.entityName + "Entity"}().${field.name}.eq(${field.name}));
  }
  
}