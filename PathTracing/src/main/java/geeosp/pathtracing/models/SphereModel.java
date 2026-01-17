/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

import geeosp.pathtracing.Algb;

/**
 * @author geeo
 */
public class SphereModel extends Model {

    double center[];
    double radius;

    //private double[] coeficients;
    private Material material;

    @Override
    public Hit getNearestIntersectionPoint(double[] origin, double[] direction) {
        Hit hit = new Hit();
        direction = Algb.normalize(direction);
        double[] co = Algb.sub(origin, center);
        double delta = Algb.dot(direction, co) * Algb.dot(direction, co) - Algb.dot(co, co) + radius * radius;
        if (delta > 0) {
            double t = -Algb.dot(direction, co);
            double deltaSqr = Math.sqrt(delta);
            double t1 = Math.max(0, t + deltaSqr);
            double t2 = Math.max(0, t - deltaSqr);
            double[] p1 = Algb.add(origin, Algb.dotByScale(t1, direction));
            double[] p2 = Algb.add(origin, Algb.dotByScale(t2, direction));
            double[] p;

            boolean ok = false;

            if (t1 > t2) {//ordena
                double aux = t2;
                t2 = t1;
                t1 = aux;
            }

            t = 0;
            if (t1 > zeroDist) {
                t = t1;
            } else if (t2 > zeroDist) {
                t = t2;
            }
            if (t > zeroDist) {



                p = Algb.add(origin, Algb.dotByScale(t, direction));
                hit.normal = Algb.normalize(Algb.sub(p, center));
                hit.point = p;
                hit.color = material.color;
                hit.model = this;

            }


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
                objectMaterial[7],
                objectMaterial[8]
        );
    }

    @Override
    public String toString() {
        String s =""
                +getName()
                + "\n center: " + center[0] + " " + center[1] + " " + center[2] + " " + center[3]
                + "\n radius: " + radius
                    + material.toString();
        ;
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


    @Override
    public double[] getColor(double[] origin, double[] point, Decoy decoy) {
        double[] ret = getColor();

        double dist = Algb.distance(origin, point);
        double scale = 1;
        switch (decoy) {
            case LINEAR:
                scale *= 1.0 / dist;
                break;
            case QUADRATIC:
                scale *= 1.0 / (dist * dist);
        }

        ret = Algb.dotByScale(scale, ret);
        ret[3] = 1;
        return ret;
    }
}
