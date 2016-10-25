#pragma once
#include <math.h>


__declspec(align(16))
struct Vector3{

public:
	  float  x;
	  float  y;
	  float  z;
private:
	float  w;
	
public:
	Vector3();
	Vector3(const float a, const float b, 
		const float c);

	~Vector3();
	Vector3& operator=(const Vector3& other);
	float &operator[](const int i);
	void normalize();
	float getNorm();


	Vector3 operator + ( const Vector3& other);
	Vector3 operator-(const Vector3& a);
	Vector3 operator*(const float f);
	Vector3 operator/(const float f);
	
	float dot(const Vector3& other);
	Vector3 cross ( Vector3& other);
	




};
