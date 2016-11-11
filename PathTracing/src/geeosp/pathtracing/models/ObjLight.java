/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

import geeosp.pathtracing.Algeb;
import java.util.Random;

/**
 *
 * @author geeo
 */
public class ObjLight extends ObjModel implements Light {

    double[] color;
    double[][] lightPoints;

    //  double intensity;
    public ObjLight() {
        super("", Type.LIGHT);

    }

    @Override
    public double[] getColor() {
    return this.color;}
    

    public ObjLight(String objectName, double[] material) {
        super(objectName, Type.LIGHT);
        this.color = new double[]{
            material[0] * material[3],
            material[1] * material[3],
            material[2] * material[3],
            1
        };
        double terc = 1.0 / 4.0;
        int a = 0;
        for (int t = 0; t < triangles.length; t++) {
            for (double i = 0; i < 1.0; i += terc) {
                for (double j = 1 - i; j > 0; j -= terc) {
                    double k = 1 - i - j;
                    a++;

                }
            }
        }
        this.lightPoints = new double[triangles.length * a][4];
        a = 0;
        for (int t = 0; t < triangles.length; t++) {
            for (double i = 0; i < 1.0; i += terc) {
                for (double j = 1 - i; j > 0; j -= terc) {
                    double k = 1 - i - j;
                    lightPoints[a]
                            = Algeb.soma(
                                    Algeb.dotByScale(i, vertices[triangles[t][0]]),
                                    Algeb.soma(Algeb.dotByScale(j, vertices[triangles[t][1]]),
                                            Algeb.dotByScale(k, vertices[triangles[t][2]])
                                    )
                            );

                    a++;

                }
            }
        }

    }

    @Override
    public double[] getColor(double[] origin, double[] target
    ) {
        double[] ret = color;
        /*
        double dist = Algeb.distance(origin, target);
        ret =Algeb.dotByScale(1/dist, color);
        ret[3]=1;
         */
        return ret;
    }

    @Override
    public double[] getOneLightPosition() {
      Random rand = new Random();  
      return lightPoints[rand.nextInt(lightPoints.length)];
    }


}
