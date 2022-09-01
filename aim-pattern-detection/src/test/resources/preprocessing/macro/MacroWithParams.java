package a.b.c;

public class A {
	<@test1 foo="a" bar="b" foobar="c"/>
	<@test2 />
	<@test3 bar="b" />
}

<#macro test1 foo bar foobar>
Macro 1, params: ${foo}, ${bar}, ${foobar}
</#macro>

<#macro test2 foo="foo", bar="bar", foobar="foobar">
Macro 2, params: ${foo}, ${bar}, ${foobar}
</#macro>

<#macro test3 bar foo="foo" foobar="foobar">
Macro 3, params: ${foo}, ${bar}, ${foobar}
</#macro>