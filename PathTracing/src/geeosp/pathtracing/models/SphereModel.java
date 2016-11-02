/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

import geeosp.pathtracing.Algeb;

/**
 *
 * @author geeo
 */
public class SphereModel extends Model {

    double center[];
    double radius;
    private double[] color;
    private double[] coeficients;

    @Override
    public Hit getNearestIntersectionPoint(double[] origin, double[] direction) {
        double hitDistanceSqr = Double.POSITIVE_INFINITY;
        Hit hit = new Hit(new double[4], new double[4], color, false);
        double t;
        double[] p;
        direction = Algeb.normalize(direction);
        double[] ominusc = Algeb.sub(origin, center);

        double delta = (Algeb.dot(direction, ominusc));
        delta *= delta;
        delta -= (Algeb.dot(ominusc, ominusc) - radius * radius);
        if (delta < 0) {

        } else if (delta == 0) {
            t = -Algeb.dot(direction, ominusc) / Algeb.dot(direction, direction);
            p = Algeb.soma(origin, Algeb.prodByEscalar(t, direction));
            hit.hitPoint = p;
            hit.color = color;
            hit.hitNormal = Algeb.normalize(Algeb.sub(p, center));
            hit.isHit = true;
        } else {
            t = -Algeb.dot(direction, ominusc) / Algeb.dot(direction, direction);
            double[] p1, p2;
            p1 = Algeb.soma(origin, Algeb.prodByEscalar(t + Math.sqrt(delta), direction));

            p2 = Algeb.soma(origin, Algeb.prodByEscalar(t - Math.sqrt(delta), direction));

            if (Algeb.distancia(p1, origin) > Algeb.distancia(p2, origin)) {
                p = p2;
            } else {
                p = p1;
            }

            hit.hitPoint = p;
            hit.color = color;
            hit.hitNormal = Algeb.normalize(Algeb.sub(p, center));
            hit.isHit = true;

        }
        return hit;
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public SphereModel(double[] center, double radius, double[] objectMaterial) {
        super("sphere", new double[3], new double[3], new double[]{1, 1, 1}, Type.OBJECT);
        this.color = new double[]{objectMaterial[0], objectMaterial[1], objectMaterial[2], 1.0};
        this.center = center;
        this.radius  = radius;
        this.coeficients = new double[]{
            objectMaterial[3],
            objectMaterial[4],
            objectMaterial[5],
            objectMaterial[6],
            objectMaterial[7]

        };
    }

    @Override
    public String toString() {
        String s = super.name
                + "\n center: " + center[0] + " " + center[1] + " " + center[2] + " " + center[3]
                + "\n radius: " + radius
                + "\n color: " + color[0] + " " + color[1] + " " + color[2] + " " + color[3]
                + "\n coeficients: " + coeficients[0] + " " + coeficients[1] + " " + coeficients[2] + " " + coeficients[3] + " " + coeficients[4] + " ";
        return  s; //To change body of generated methods, choose Tools | Templates.
    }

}
