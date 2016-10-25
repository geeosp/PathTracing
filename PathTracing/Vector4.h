#pragma once


struct Vector4

{

	

public:
	  float  x;
	  float  y;
	  float  z;
	  float  w;
	





	Vector4();
	~Vector4();
	Vector4& operator=(const Vector4& other);
	float &operator[](const int i) {
		float * ptr = &x;
		return ptr[i];
	}

};
