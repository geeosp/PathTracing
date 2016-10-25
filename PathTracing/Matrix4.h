#pragma once
#include <string.h>
#include "Vector3.h"

__declspec(align(16))
class Matrix4
{
private:
	float a, b, c, d,
		e, f, g, h,
		i, j, k, l,
		m, n, o, p;



public:
	Matrix4();
	Matrix4(float* array);
	~Matrix4();
	
	Vector3 &operator[](const int i);

	Matrix4 & operator=(const Matrix4& other);
	void transpose();
	
	Matrix4 operator + (const Matrix4& other);
	Matrix4 operator -(const Matrix4& other);
	
	Vector3 operator*(const Vector3 v);
	/*
	Vector3 operator*(const float f);
	
	Vector3 operator/(const float f);


*/
};



