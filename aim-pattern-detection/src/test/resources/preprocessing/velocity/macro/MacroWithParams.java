public class A {
	#testMacro("i" "j")
}

#macro( testMacro $arg1 $arg2 )
private int $arg1;
private int $arg2;
#end