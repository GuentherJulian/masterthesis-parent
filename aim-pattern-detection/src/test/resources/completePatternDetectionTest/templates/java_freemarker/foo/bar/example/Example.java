package foo.bar.example;

import java.util.ArrayList;
import java.util.List;

public class Example {
	
	private List<String> ${varName};
	
	public Example() {
		this.${varName} = new ArrayList<>();
	}
	
	public List<String> getStrings() {
		return this.${varName};
	}
	
}