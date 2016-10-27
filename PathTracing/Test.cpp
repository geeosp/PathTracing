#include "Test.h"




void Test::run()
{
	Vector3  v1(1, 2, 3);
	assert(v1[0] == 1);
	assert(v1[1] == 2);
	assert(v1[2] == 3);

	Vector3  v2(5, 9, 3);
	
	Vector3  v3, v4, v5, v6;
	
	v3 = v1 + v2;
	assert(v3[0] == 6);
	assert(v3[1] == 11);
	assert(v3[2] == 6);
	
	v4 = v1 - v2;
	assert(v4[0] == -4);
	assert(v4[1] == -7);
	assert(v4[2] == 0);

	v5 = v1 * 4;
	assert(v5[0] == 4);
	assert(v5[1] == 8);
	assert(v5[2] == 12);

	v6 = v1 / 4.f;
	assert(v6[0] == .25f);
	assert(v6[1] == .5f);
	assert(v6[2] == .75f);

	float h = v1[2];
	assert(h == 3);
	v1[2] = 6;
	h = v1[2];
	assert(h == 6);


	float* array = new float[16];
	for (int i = 0; i < 16; i++) {
		array[i] = i + 1;
	}
	
	Matrix4 m1(array);
	Matrix4 m2 = m1;




	m1.transpose();
	Matrix4 identity;
	for (int i = 0; i < 4; i++) {
		identity[i][i] = 1.0f;
	}
	Matrix4 a23 = m1 - m2;
	Matrix4 a3 = identity*m1;


}
