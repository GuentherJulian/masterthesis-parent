public class A {
	#if( $anything )
    private int foo;
    #end
	#testMacro();
}

#macro( testMacro )
private int j;
#end