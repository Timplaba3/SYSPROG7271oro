#include <iostream>
#include <ctime>
#include <cstdlib>

using namespace std;

int** delrc(int** arr, int m, int n, int mx, int nx) {// 

    int** array2 = new int* [m-1];
    for (int i = 0; i < m-1; i++) {
        array2[i] = new int[n-1];
    }
    int i, j;

    asm (
        //.intel_syntax noprefix
    "start:\n\t"
        "mov ebx, 0\n\t" //i
            "mov ecx, 0\n\t" //j
            "mov %1, 0\n\t"
            "mov %2, 0\n\t"
"row_check :\n\t"
        "cmp ebx, %6\n\t"
            "je skip_row\n\t"
            "cmp ecx, %5\n\t"
            "je skip_column\n\t"
            "jmp rewrite\n\t"

            "skip_column :\n\t"
            "inc ecx\n\t"
            "jmp row_check\n\t"


            "rewrite :\n\t"
            "xor edx, edx\n\t"
            "mov eax, %7[0]\n\t"
            "mov eax, [eax + ebx * 4]\n\t" // column i
            "mov edx, [eax + ecx * 4]\n\t" //row j

"push ebx\n\t"
            "push ecx\n\t"

                "mov ebx, %1\n\t"
                "mov ecx, %2\n\t"

            "mov eax, %0[0]\n\t"
            "mov eax, [eax + ebx * 4]\n\t"
            "mov [eax + ecx * 4], edx\n\t"

                "inc ecx\n\t"
                "mov %2, ecx\n\t"
                
            "pop ecx\n\t"
            "pop ebx\n\t"
            "jmp con\n\t"

        "con:\n\t"
        "inc ecx\n\t"
"cmp ecx, %3\n\t"
            "ja next_row\n\t"
            "jmp row_check\n\t"

            "skip_row:\n\t"
            "mov %2, 0\n\t"
            "mov ecx, 0\n\t"
            "inc ebx\n\t"
            "cmp ebx, %4\n\t"
            "je _close\n\t"
            "jmp row_check\n\t"

    "next_row :\n\t"
        "mov %2, 0\n\t"
            "mov ecx, %1\n\t"
"inc ecx\n\t"
            "mov %1, ecx\n\t"
            "mov ecx, 0\n\t"
            "inc ebx\n\t"
            "cmp ebx, %4\n\t"
            "je _close\n\t"
            "jmp row_check\n\t"

            "_close :\n\t"
:"=m"(array2)
:"m"(i),
"m"(j),
"m"(n),
"m"(m),
"m"(nx),
"m"(mx),
"m" (arr)
: "eax"


    );

    return array2;

}

int main()
{
    srand(time(0));
    int n = rand() % 9 + 2;//5
        int m = rand() % 9 + 2;//2

    int** arr = new int* [m];
    for (int i = 0; i < m; i++) {
        arr[i] = new int[n];
    }

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            arr[i][j] = rand() % 89 + 10;
            cout << arr[i][j] << " ";
        }
        cout << endl;
    }
    int mx = rand() % m, nx = rand() % n;//1, nx = 3;
    cout << endl << "mx: " << mx << " nx: " << nx << endl;
    int** array3;
    array3 = delrc(arr, m, n, mx, nx);
    cout << endl;
    for (int i = 0; i < m - 1; i++) {
        for (int j = 0; j < n - 1; j++) {
            cout << array3[i][j] << " ";
        }
        cout << endl;
    }

    for (int i = 0; i < m; i++) {
        delete[] arr[i];
    }
    delete[] arr;
    
    for (int i = 0; i < m - 1; i++) {
        delete[] array3[i];
    }
    delete[] array3;

    return 0;
}


