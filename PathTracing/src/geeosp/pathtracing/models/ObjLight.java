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
    public double[] getColor(double[] origin, double[] target) {
        double [] ret = color;
        /*
        double dist = Algeb.distance(origin, target);
        ret =Algeb.dotByScale(1/dist, color);
        ret[3]=1;
        */
        return ret;
    }

}
