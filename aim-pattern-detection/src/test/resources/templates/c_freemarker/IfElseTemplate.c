#include <stdio.h>

<#if condition1>
int main(int argc, char *argv[]) {
	printf("Hello World 1");
	
    <#if condition2>
    int i = 42;
    <#else>
    float j = 4.2f;
    </#if>

    return 0;
}
<#else>
int main(int argc, char *argv[]) {
	printf("Hello World 2");
    return 0;
}
</#if>