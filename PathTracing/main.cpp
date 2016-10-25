#include "main.h"
#include "Vector4.h"
#include <iostream>

using namespace std;
int main(int argc, int* argv) {
	Vector4* v = new Vector4();
	v->x = 10;
	v->z = 32;
	cout << (*v)[2] << endl;



	return 0;
}