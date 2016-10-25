#include "Vector4.h"
#include <stdio.h>
#include <string.h>


Vector4::Vector4()
{
	
	memset(&x, 0.0f, 4*sizeof(float));
	
}


Vector4::~Vector4()
{
	delete &x;
	delete &y;
	delete &z;
	delete &w;
	
}

Vector4& Vector4::operator=(const Vector4& other) {
	

	return *this;
}




