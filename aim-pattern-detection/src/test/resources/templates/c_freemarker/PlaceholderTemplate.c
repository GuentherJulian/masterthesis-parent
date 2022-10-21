#include <stdio.h>

int main(int argc, char *argv[]) {
    int var1 = ${var1_value};
    float ${var2} = 4.2f;
    
    printf("%d \n", var1); 
    printf("%f \n", ${var2}); 

    return 0;
}