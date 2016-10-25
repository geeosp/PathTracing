#pragma once
#include <string.h>
#include "Vector3.h"

__declspec(align(16))
class Matrix4
{
private:
	float a, b, c, d,
		e, f, g, h,
		i, j, k, l;



public:
	Matrix4();
	~Matrix4();
	


	Vector3 &operator[](const int i);

	Matrix4& operator=(const Matrix4& other);
	void transpose();
	
	/*
	
	
	
	float getNorm();


	Vector3 operator + (const Vector3& other);
	Vector3 operator-(const Vector3& a);
	Vector3 operator*(const float f);
	Vector3 operator/(const float f);

	float dot(const Vector3& other);
	Vector3 cross(Vector3& other);*/




};



