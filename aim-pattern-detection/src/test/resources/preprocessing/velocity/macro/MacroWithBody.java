public class A {
	#@testMacro()foo#end
}

#macro( testMacro )
private int $!bodyContent;
#end