#include <stdio.h>

int test1(int i);
void test2(char* str) {
    printf("%s", str);
}

int main() {
    int i;
    int j;
    char* ${str} = "hello world";

    j = test1(j);

    char* foo = "foo";
    test2(foo);

    <#if anything>
    int foo = 42;
    <#else>
    int foo = 84;
    </#if>

    return 0;
}

int test1(int i) {
    return i + 42;
}
