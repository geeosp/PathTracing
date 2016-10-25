#include "main.h"

using namespace std;

int main(int argc, int* argv) {

	Vector3  s (1,1,2);
	Vector3  t (5, 9, 3);
	Vector3  a,b,c,d;
	a = s + t;
	b = s - t;
	c = s * 2.f;
	d = t / 4.f;
	float h = a[2];
	a[2] = 6;

	float* array = new float[16];
	for (int i = 0; i < 16; i++) {
		array[i] = i+1;
	}
	Matrix4 k(array);
	Matrix4 k2=k;
	k.transpose();

	Matrix4 a23 = k - k2;
	
	return 0;
}