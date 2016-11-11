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
public class SphereModel extends Model implements DifuseModel {

    double center[];
    double radius;
    private double[] color;
    private double[] coeficients;

    @Override
    public Hit getNearestIntersectionPoint(double[] origin, double[] direction) {
        Hit hit = new Hit();
        direction = Algeb.normalize(direction);
        double a = 1;
        double[] co = Algeb.sub(origin, center);
        double b = 2 * Algeb.dot(direction, co);
        double c = Algeb.dot(co, co) - radius * radius;
        double delta = b * b - 4 * a * c;
        double t;
        double[] p = null;
        if (delta == 0) {
            t = -.5 * b / a;
            p = Algeb.soma(origin, Algeb.dotByScale(t, direction));

        } else if (delta > 0) {
            double sqrDelta = Math.sqrt(delta);
            double[] p1, p2;
            t = (-b + sqrDelta) / (2 * a);
            p1 = Algeb.soma(origin, Algeb.dotByScale(t, direction));
            t = (-b - sqrDelta) / (2 * a);
            p2 = Algeb.soma(origin, Algeb.dotByScale(t, direction));

            if (Algeb.distance(p1, origin) < Algeb.distance(p2, origin)) {
                p = p1;
            } else {
                p = p2;
            }

            hit.point = p;
            hit.normal = Algeb.normalize(Algeb.sub(p, center));
            hit.color = color;
            hit.model = this;
        }
        return hit;

    }

    public SphereModel(double[] center, double radius, double[] objectMaterial) {
        super("sphere", new double[3], new double[3], new double[]{1, 1, 1}, Type.OBJECT);
        this.color = new double[]{objectMaterial[0], objectMaterial[1], objectMaterial[2], 1.0};
        this.center = center;
        this.radius = radius;
        this.coeficients = new double[]{
            objectMaterial[3],
            objectMaterial[4],
            objectMaterial[5],
            objectMaterial[6],
            objectMaterial[7]

        };
    }

     @Override
    public double getKa() {
        return coeficients[0];
    }

    @Override
    public double getKt() {
        return coeficients[3];
    }

    @Override
    public double getKd() {
        return coeficients[1];
    }

    @Override
    public double getKs() {
        return coeficients[2];
    }

    @Override
    public String toString() {
        String s = super.name
                + "\n center: " + center[0] + " " + center[1] + " " + center[2] + " " + center[3]
                + "\n radius: " + radius
                + "\n color: " + color[0] + " " + color[1] + " " + color[2] + " " + color[3]
                + "\n coeficients: " + coeficients[0] + " " + coeficients[1] + " " + coeficients[2] + " " + coeficients[3] + " " + coeficients[4] + " ";
        return s; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getCoeficients() {
        return this.coeficients;
    }

    @Override
    public double[] getColor(double[] origin, double[] target) {
        return this.color;
    }

    @Override
    public double[] getColor() {
       return this.color;
    }

}
