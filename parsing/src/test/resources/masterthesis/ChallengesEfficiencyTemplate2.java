public class Foo extends SuperFoo implements ${interfaces} {
	private String ${fieldName};
	
	public String ${'get'+ fieldName}() {
		return this.${fieldName};
	}
}