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
  

    }

    @Override
    public double[] getColor(double[] origin, double[] target
    ) {
        double[] ret = color;
      
        return ret;
    }

    @Override
    public double[] getOneLightPosition() {
      Random rand = new Random();  
      double i = rand.nextDouble();
      double k = rand.nextDouble()*(1-i);
      double j = 1 -k -i;
      int [] t = triangles[rand.nextInt(triangles.length)];
      double[] p = Algeb.dotByScale(i, vertices[t[0]]);
       p =Algeb.soma(p, Algeb.dotByScale(j,vertices[t[1]]));
       p = Algeb.soma(p, Algeb.dotByScale(k, vertices[t[2]]));
      return p;
    }


}
