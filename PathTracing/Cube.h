#pragma once
#include "Model3D.h"
__declspec(align(16))
class Cube: public Model3D
{
private:
		Vector3 p1;
		Vector3 p2;
public:
	

	Cube(Vector3 p1, Vector3 p2);
	~Cube();
	

	// Inherited via Model3D
	virtual Vector3 getNearestPoint(const Vector3 point) override;

	virtual Vector3 getNearestPoint(const Vector3 origin, const Vector3 direction) override;

	virtual Vector3 getFirstIntercection(const Vector3 origin, const Vector3 direction) override;

};

