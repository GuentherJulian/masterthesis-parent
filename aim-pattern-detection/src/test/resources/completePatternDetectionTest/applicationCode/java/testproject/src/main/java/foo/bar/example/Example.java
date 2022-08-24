package foo.bar.example;

import java.util.ArrayList;
import java.util.List;

public class Example {
	
	private List<String> strings;
	
	public Example() {
		this.strings = new ArrayList<>();
	}
	
	public List<String> getStrings() {
		return this.strings;
	}
	
}