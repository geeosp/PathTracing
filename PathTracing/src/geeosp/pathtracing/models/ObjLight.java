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
        //    this.intensity = material[3];

    }

    @Override
    public double[] getColor() {
        double [] ret = color;
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
        double j = 1 - k - i;
        int[] t = triangles[rand.nextInt(triangles.length)];
        double[] p = Algb.dotByScale(i, vertices[t[0]]);
        p = Algb.soma(p, Algb.dotByScale(j, vertices[t[1]]));
        p = Algb.soma(p, Algb.dotByScale(k, vertices[t[2]]));
        return p;
    }

}
