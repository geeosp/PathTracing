#include "Matrix4.h"



Matrix4::Matrix4()
{
	
}


Matrix4::~Matrix4()
{
}

Vector3 & Matrix4::operator[](const int i)
{
		float * ptr = &a;
	if (i < 4) {
		for (int m = 0; m < i; m++) {
			ptr += 4;
		}
	}
	Vector3* ptv =(Vector3*) ptr;
	return *ptv;
}

Matrix4 & Matrix4::operator=(const Matrix4 & other)
{
	memcpy(&a, &other.a,16*sizeof(float));
	return *this;
}

void Matrix4::transpose()

{
	for (int a1 = 0; a1 < 4; a1++) {
		for (int a2 = 0; a2 < a1; a2++) {
			f
		}
	}


}









