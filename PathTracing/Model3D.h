#pragma once
#include "Vector3.h"
 

struct IntersectionPoint {
public:
	Vector3 point;
	Vector3 normal;
	Vector3 color;
};


class Model3D
{
public:

	Model3D();
	~Model3D();

	virtual Vector3 getNearestPoint(Vector3 point)=0;
	virtual Vector3 getNearestPoint(Vector3 origin, Vector3 direction) = 0;
	virtual Vector3 getFirstIntercection(Vector3 origin, Vector3 direction) = 0;


};

