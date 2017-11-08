#include <iostream>
#include <string>
#include <vector>
#include <string.h>
#include <stdio.h>

using namespace std;

vector<string> split(string text, string delimiter) {
    vector<std::string> result;
    char* c_str = const_cast<char*>(text.c_str());
    char* current;
    current = strtok(c_str, delimiter.c_str());
    while(current != NULL) {
        result.push_back(current);
        current = strtok(NULL, delimiter.c_str());
    }
    return result;
}


int main()
{
    string text = "This is a test, does this work?";
    string delimiter = ",";
    vector<string> res = split(text, delimiter);
    for(int i=0;i<res.size();i++) {
        cout << res[i] << endl;
    }
    return 0;
}
