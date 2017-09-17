#include <iostream>
#include <stdio.h>

using namespace std;

int main()
{
    int r,c;
    if(scanf("%d %d", &r, &c) != 2) {
        exit(1);
    }
    char lot[r][c+1];
    for(int i=0;i<r;i++) {
        if(scanf("%s", lot[i]) != 1) {
            exit(1);
        }
    }
    int counters[] = {0,0,0,0,0};
    for(int i=0;i<r-1;i++) {
        for(int j=0;j<c-1;j++) {
            if(lot[i][j] != '#' && lot[i][j+1] != '#' && lot[i+1][j] != '#' && lot[i+1][j+1] != '#') {
                counters[(lot[i][j] == 'X' ? 1:0) + (lot[i][j+1] == 'X' ? 1:0) + (lot[i+1][j] == 'X' ? 1:0) + (lot[i+1][j+1] == 'X' ? 1:0)]++;
            }
        }
    }
    for(int i=0;i<5;i++) {
        cout << counters[i] << endl;
    }
    return 0;
}
