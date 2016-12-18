/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

import geeosp.pathtracing.Algb;
import java.util.Random;

/**
 *
 * @author geeo
 */
public class ObjLight extends ObjModel {

    double[] color;
    Material material;

    //  double intensity;
    public ObjLight() {
        super("", Type.LIGHT);
    }

    public ObjLight(String objectName, double[] material) {
        super(objectName, Type.LIGHT);
        this.color = new double[]{
            material[0] * material[3],
            material[1] * material[3],
            material[2] * material[3],
            1
        };
        this.material = new Material(this.color, 0, 0, 0, 0, 0, 1);
        //    this.intensity = material[3];

    }

    @Override
    public double[] getColor() {
        double[] ret = color;
        /*
        double dist = Algeb.distance(origin, target);
        ret =Algeb.dotByScale(1/dist, color);
        ret[3]=1;
         */
        return ret;
    }

    public double[] getOnePoint() {
        Random rand = new Random();
        double i = rand.nextDouble();
        double k = rand.nextDouble() * (1 - i);
        int pos = rand.nextInt(triangles.length);

  

        double j = 1 - i - k;
        int[] t = triangles[pos];
        double[] p = Algb.dotByScale(i, vertices[t[0]]);
        p = Algb.soma(p, Algb.dotByScale(j, vertices[t[1]]));
        p = Algb.soma(p, Algb.dotByScale(k, vertices[t[2]]));
        return p;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }


    @Override
    public double[] getColor(double[] origin, double[] point, Decoy decoy) {
        double[] ret = color;

        double dist = Algb.distance(origin, point);
        double scale = 1;
        switch (decoy){
            case LINEAR:
                scale*= 1.0/dist;
                break;
            case QUADRATIC:
                scale*=1.0/(dist*dist);
                break;
        }

        ret =Algb.dotByScale(scale, ret);
        ret[3]=1;
        return ret;
    }
}
