#include <iostream>
#include <stdio.h>

using namespace std;

int main()
{
    int a, i;
    if(scanf("%d %d", &a, &i) != 2) {
        return 0;
    }
    cout << a*(i-1)+1 << endl;
    return 0;
}
