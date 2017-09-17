#include <iostream>
#include <stdio.h>

using namespace std;

int main()
{
    int a,b,c,d,e,f,g,h,i,j;
    if(scanf("%d %d %d %d %d %d %d %d %d %d", &a, &b, &c, &d, &e, &f, &g, &h, &i, &j) != 10) {
        return 0;
    }
    int result[] = {a,b,c,d,e,f,g,h,i,j};
    for(int i=0;i<10;i++) {
        result[i] = result[i]%42;
    }
    int amount = 10;
    for(int i=0;i<10;i++) {
        for(int j=i+1;j<10;j++) {
            if(result[i] == result[j] && result[i] != 43) {
                amount--;
                result[j] = 43;
            }
        }
    }
    cout << amount << endl;
    return 0;
}
