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
    double intensity;

    public ObjLight(String objectName, double[] material) {
        super(objectName, Type.LIGHT);
        this.color = new double[]{
            material[0],
            material[1],
            material[2],
            1
        };
        this.intensity = material[3];

    }

    @Override
    public double[] getColor(double[] origin, double[] target) {
       // throw new UnsupportedOperationException();
        return Algeb.prodByEscalar(intensity, color);
    }

}
