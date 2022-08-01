public class TestClass {

  private int i;

  public void permissionCheckAnnotationPresent() {
    assertions.assertThat(hasAnnotation)
        .as("Method " + method.getName() + " in Class " + clazz.getSimpleName() + " is missing access control")
        .isTrue();
  }
}