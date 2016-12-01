/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

import geeosp.pathtracing.Algb;

/**
 *
 * @author geeo
 */
public class SphereModel extends Model {

    double center[];
    double radius;

    private double[] coeficients;
    private Material material;

    @Override
    public Hit getNearestIntersectionPoint(double[] origin, double[] direction) {
        Hit hit = new Hit();
        direction = Algb.normalize(direction);
        double a = 1;
        double[] co = Algb.sub(origin, center);
        double b = 2 * Algb.dot(direction, co);
        double c = Algb.dot(co, co) - radius * radius;
        double delta = b * b - 4 * a * c;
        double t;
        double[] p = null;
        if (delta == 0) {
            t = -.5 * b / a;
            p = Algb.soma(origin, Algb.dotByScale(t, direction));

        } else if (delta > 0) {
            double sqrDelta = Math.sqrt(delta);
            double[] p1, p2;
            t = (-b + sqrDelta) / (2 * a);
            p1 = Algb.soma(origin, Algb.dotByScale(t, direction));
            t = (-b - sqrDelta) / (2 * a);
            p2 = Algb.soma(origin, Algb.dotByScale(t, direction));

            if (Algb.distance(p1, origin) < Algb.distance(p2, origin)) {
                p = p1;
            } else {
                p = p2;
            }

            hit.point = p;
            hit.normal = Algb.normalize(Algb.sub(p, center));
            hit.color = material.color;
            hit.model = this;
        }
        return hit;

    }

    public SphereModel(double[] center, double radius, double[] objectMaterial) {
        super("sphere", new double[3], new double[3], new double[]{1, 1, 1}, Type.OBJECT);

        this.center = center;
        this.radius = radius;

        this.material = new Material(new double[]{
            objectMaterial[0],
            objectMaterial[1],
            objectMaterial[2],
            1.0},
                objectMaterial[3],
                objectMaterial[4],
                objectMaterial[5],
                objectMaterial[6],
                objectMaterial[7]
        );
    }

    @Override
    public String toString() {
        String s = super.name
                + "\n center: " + center[0] + " " + center[1] + " " + center[2] + " " + center[3]
                + "\n radius: " + radius
           //     + "\n color: " + color[0] + " " + color[1] + " " + color[2] + " " + color[3]
                + "\n coeficients: " + coeficients[0] + " " + coeficients[1] + " " + coeficients[2] + " " + coeficients[3] + " " + coeficients[4] + " ";
        return s; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getColor() {
        return this.material.color;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

}
