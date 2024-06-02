#include <stdio.h>
#include <string.h>

 extern "C" bool predict_call(const char *ptr, char * );
  int main(int argc, char **argv ) {
    char Temp[1024];
    if ( argc <=1 ) {
        printf("We need more command line arguments\n");
        return -1;
    }
    memset(Temp,0,1024);
    for(int i=1; i<argc; i++ ) {
        strcat(Temp,argv[i]);
        strcat(Temp,",");
    }
    char output[255];
    predict_call(Temp,output);
    printf("%s\n", output);
 }


