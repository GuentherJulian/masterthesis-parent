package a.b.c.d;

import abc.def.ghi.jkl;
import xyz.uvw.rst;
import foo.bar.foobar;

public class ${className} {

	private int i;
	private int j;
	private String ${str} = "hello world";

	public ${className}(){

	}

	public void test1() {
		int foo = 1;
		foo = foo + 42;
		System.out.println(foo);
	}

	public int test2() {
		int bar = 1;
		foo = foo + 42;
		return foo;
	}

	<#if getter>
	public int getI() {
		return this.i;
	}
	</#if>

	<#if anything>
	public String toString() {
		return "hello";
	}
	<#else>
	public String toString() {
		return "world";
	}
	</#if>
}