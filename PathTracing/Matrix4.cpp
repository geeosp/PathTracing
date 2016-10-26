#include "Matrix4.h"



Matrix4::Matrix4()
{
	a = b = c = d = e = f = g = h = i = j = k = l = m = n = o = p = 0.0f;
}

Matrix4::Matrix4(float* array)
{
	memcpy(&a, array, 16 * sizeof(float));
}


Matrix4::~Matrix4()
{
}

Vector3 & Matrix4::operator[]( int i)
{
	Vector3 * ptr = (Vector3*)&a;
	return ptr[i];

}


Matrix4 & Matrix4::operator=(const Matrix4 & other)
{
	memcpy(&a, &other.a,16*sizeof(float));
	return *this;
}

void Matrix4::transpose()

{
	float t = 0.f;
	Matrix4* me = this;
	for (int a1 = 0; a1 < 4; a1++) {
		for (int a2 = 0; a2 < a1; a2++) {
			t = me->operator[](a1)[a2];
			me->operator[](a1)[a2] = me->operator[](a2)[a1];
			me->operator[](a2)[a1] = t;
		}
	}
}

Matrix4 Matrix4::operator+(const Matrix4 & other)
{
	Matrix4* ret = new Matrix4();
	float * aux = (float *)ret;

	float *aux2 = (float*)&other;
	for (int i = 0; i < 16; i++) {
		aux[i] = ((float*)this)[i] +aux2[i];
	}
	return *ret;
}

Matrix4 Matrix4::operator-(const Matrix4 & other)
{
	Matrix4* ret = new Matrix4();
	float * aux = (float *)ret;

	float *aux2 = (float*)&other;
	for (int i = 0; i < 16; i++) {
		aux[i] = ((float*)this)[i] - aux2[i];
	}
	return *ret;
}

Vector3 Matrix4::operator*(const Vector3 v)
{
	Vector3 resp;
	for (int i = 0; i < 4; i++) {
			Vector3 l = this->operator[](i);
			resp[i] = l.dot(v);
	}

	return resp;
}

Matrix4 Matrix4::operator*( Matrix4 v)
{
	Matrix4 resp;
	Matrix4 n(*this);
	
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			resp[i][j] = n[i].dot( Vector3(v[0][j], v[1][j], v[2][j], v[3][j]));
		}
	}



	return resp;
}

void Matrix4::indentify()
{
	float id[] =
	{
		1.f, 0.f, 0.f, 0.f,
			0.f, 1.f, 0.f, 0.f,
			0.f, 0.f, 1.f, 0.f,
			0.f, 0.f, 0.f, 1.f
	};
	memcpy(&a, id, 16 * sizeof(float));
}









