#include "main.h"
#include "Vector3.h"
#include <iostream>

using namespace std;

int main(int argc, int* argv) {

	Vector3  s (1,1,2);
	Vector3  t (5, 9, 3);
	Vector3  a,b,c,d;
	a = s + t;
	b = s - t;
	c = s * 2.f;
	d = t / 4.f;
	return 0;
}