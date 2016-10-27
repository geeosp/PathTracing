#pragma once
#include "Vector3.h"
 
__declspec(align(16))
struct IntersectionPoint {
public:
	Vector3 point;
	Vector3 normal;
	Vector3 color;
};


class Model3D
{
public:
	Vector3 position;
	Vector3 rotation;
    Vector3 scale;



	Model3D();
	virtual ~Model3D()=0;
	virtual Vector3 getNearestPoint(const Vector3 point)=0;
	virtual Vector3 getNearestPoint(const Vector3 origin,const  Vector3 direction) = 0;
	virtual Vector3 getFirstIntercection(const Vector3 origin, const Vector3 direction) = 0;



};

