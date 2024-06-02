#include <stdio.h>
#include <string.h>
#include <conio.h>

 extern "C" bool predict_call(const char *ptr, char * );

 void readFile() {
     FILE* fp = fopen("file_path", "r");
  	if (!fp)
  		printf("Can't open file\n");

  	else {
  		// Here we have taken size of
  		// array 1024 you can modify it
  		char buffer[1024];

  		int row = 0;
  		int column = 0;

  		while (fgets(buffer,1024, fp)) {
  			String rs = "";

  			// To avoid printing of column
  			// names in file can be changed
  			// according to need
  			if (row == 1)
  				continue;

  			// Splitting the data
  			char* value = strtok(buffer, ", ");

  			while (value) {
  				rs += args[c]+",";
  			}
            multipleInstance(rs);
  			printf("\n");
  		}

  		// Close the file
  		fclose(fp);
  	}
  }

  void multipleInstance() {
  }

  void singleInstance(int argc, char **argv) {
    char Temp[1024];
    memset(Temp,0,1024);
     for(int i=1; i<argc; i++ ) {
        strcat(Temp,argv[i]);
        strcat(Temp,",");
     }
     char output[255];
     predict_call(Temp,output);
     printf("%s\n", output);
  }

  int main(int argc, char **argv ) {
    if ( argc <=1 ) {
        printf("We need more command line arguments\n");
        return -1;
    }
    singleInstance(int argc, char **argv);

 }





