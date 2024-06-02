#include <iostream>
#include <string>
#include <string.h>
using namespace std;

string predict( string ARGS );
void find_str(string s, string del,int offset,string &result) {
    // Use find function to find 1st position of delimiter.

    int end = s.find(del);
    int i=0;
    while (end != -1) { // Loop until no delimiter is left in the string.
        string temp = s.substr(0, end);
        if ( i++ == offset ) { result =  temp;  return; }
        s.erase(s.begin(), s.begin() + end + 1);
        end = s.find(del);
    }
    result =  s.substr(0, end);
}



string interpret(int offset, string args ) {

   string **arr= new string *[100];
  // for(int i=0;i<100;++i) { *arr[i] = (string)""; }

   int cnt = 0;
   string result ="";
   find_str(args,",",offset,result);
   return result;


}

string cmd_get_s( int offset,string args ) {

	return interpret(offset,args);
 }

int cmd_get_i( int offset, string S ) {
	string n = interpret(offset,S);
	return atoi(n.c_str());
 }

double cmd_get_d(int offset, string S ) {
	string n = interpret(offset,S);
	return atof(n.c_str());
 }

bool cmd_get_b( int offset,string S ) {
	string n = interpret(offset,S);
	return n == "TRUE"? true:false;
}

extern "C"  bool predict_call(const char *str, char *str2 ) {
   string ret = predict(string(str));
   strcpy(str2, ret.c_str());
   return true;
}


int main(int argc , char **argv) {
	if ( argc <= 1 ) {
		return 0;
	}
	string strs = "";
	for(int i=1; i<argc; ++ i )
	{
		strs += string(argv[i]) + "|";
	}
        cout << strs << endl;
	string rs = predict(strs);
	cout << "Predicted ....." << rs << "$$$" << endl;
	return 0;

 }


#include <iostream> 

#include <string> 
using namespace std;
string predict( string ARGS ) { 
	double VAR_9;
	double VAR_8;
	double VAR_1;
	double VAR_0;
	double VAR_3;
	double VAR_12;
	double VAR_4;
	double VAR_7;
	double VAR_6;
	VAR_9 = cmd_get_d(9.0, ARGS);
	VAR_8 = cmd_get_d(8.0, ARGS);
	VAR_1 = cmd_get_d(1.0, ARGS);
	VAR_0 = cmd_get_d(0.0, ARGS);
	VAR_3 = cmd_get_d(3.0, ARGS);
	VAR_12 = cmd_get_d(12.0, ARGS);
	VAR_4 = cmd_get_d(4.0, ARGS);
	VAR_7 = cmd_get_d(7.0, ARGS);
	VAR_6 = cmd_get_d(6.0, ARGS);
	if ( VAR_6>=6.5 ) { 
	return "Y";
	} 
	 else { 
 	if ( VAR_6>=5.7 ) { 
	if ( VAR_3>=51.0 ) { 
	if ( VAR_8>=2.9 ) { 
	if ( VAR_0>=363.0 ) { 
	return "Y";
	} 
	 else { 
 	return "P";
	} 
  
 
	} 
	 else { 
 	return "Y";
	} 
  
 
	} 
	 else { 
 	return "P";
	} 
  
 
	} 
	 else { 
 	if ( VAR_12>=25.0 ) { 
	return "Y";
	} 
	 else { 
 	if ( VAR_7>=5.0 ) { 
	if ( VAR_8>=2.0 ) { 
	return "Y";
	} 
	 else { 
 	if ( VAR_4>=7.7 ) { 
	return "Y";
	} 
	 else { 
 	if ( VAR_9>=1.6 ) { 
	if ( VAR_1>=45383.0 ) { 
	return "N";
	} 
	 else { 
 	return "Y";
	} 
  
 
	} 
	 else { 
 	return "N";
	} 
  
 
	} 
  
 
	} 
  
 
	} 
	 else { 
 	return "N";
	} 
  
 
	} 
  
 
	} 
  
 
	} 
  
 
 }

