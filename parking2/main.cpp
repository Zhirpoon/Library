#include <iostream>

using namespace std;

int main()
{
    int amtTests;
    int amtStores;
    cin >> amtTests;
    while(amtTests--) {
        int max = -1;
        int min = 100;
        cin >> amtStores;
        while(amtStores--) {
            int position;
            cin >> position;
            min = position < min ? position : min;
            max = position > max ? position : max;
        }
        cout << 2*(max-min) << endl;
    }
    return 0;
}
