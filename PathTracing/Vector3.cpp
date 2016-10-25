#include "Vector3.h"


Vector3::Vector3()
{
	x = 0.0f;
	y = 0.f;
	z = 0.f;
	w = 1.0f;
}

Vector3::Vector3(const float a, const float b, const float c)
{
	x = a;
	y = b;
	z = c;
	w = 1.0f;
}
Vector3::Vector3(const float a, const float b, const float c, const float d)
{
	x = a;
	y = b;
	z = c;
	w = d;
}


Vector3::~Vector3()
{
	/*
	delete &x;
	delete &y;
	delete &z;
	delete &w;
	*/
}



void Vector3::normalize()
{
	float t = getNorm();
	x = x / t;
	y = y / t;
	z = z / t;

}

float Vector3::getNorm()
{
	return sqrtf(x*x+y*y+z*z);
}


Vector3& Vector3::operator=(const Vector3& other) {
	x = other.x;
	y = other.y;
	z = other.z;
	w = other.w;
	return *this;
}

float &  Vector3::operator[](const int i)
{
	float * ptr = &x;
	return ptr[i];

}


Vector3 Vector3::operator + (const Vector3& other) {
	Vector3 a = *this;
	a.x += other.x;
	a.y += other.y;
	a.z += other.z;
	return a;




}
Vector3 Vector3::operator - (const Vector3& other) {
	Vector3 a = *this;
	a.x -= other.x;
	a.y -= other.y;
	a.z -= other.z;
	return a;
}

Vector3 Vector3::operator*(const float f)
{
	 Vector3 r = *this;
	 r.x *= f;
	 r.y *= f;
	 r.z *= f;
	 return r;
}

Vector3 Vector3::operator/(const float f)
{
	Vector3 r = *this;
	r.x /= f;
	r.y /= f;
	r.z /= f;
	return r;
}

float Vector3::dot(const Vector3 & other)
{
	float r = this->x*other.x+ 
		this->y*other.y+
		this->z*other.z;
	return r;
}

Vector3  Vector3::cross(  Vector3 & b)
{
	Vector3 a =*this;
	Vector3 resp;
	resp[0] = a[1] * b[2] - a[2] * b[1];
	resp[1] = a[2] * b[0] - a[0] * b[2];
	resp[2] = a[0] * b[1] - a[1] * b[0];


	return Vector3();
}






