/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

/**
 *
 * @author Geovane
 */
public class Material {

     public double ka;
     public double kd;
     public double ks;
     public double kt;
     double n;
     public double[] color;

    public Material(double[] color, double ka, double kd, double ks, double kt, double n) {
        this.ka = ka;
        this.kd = kd;
        this.ks = ks;
        this.kt = kt;
        this.n = n;
        this.color = color;
    }

}
