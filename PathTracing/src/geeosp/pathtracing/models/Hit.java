/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

/**
 *
 * @author geeo
 */
public class Hit {

    public double[] hitPoint;
    public double[] hitNormal;
    public double[] color;
    public boolean isHit;

    public Hit(double[] hitPoint, double[] hitNormal, double[] color, boolean isHit) {
        this.hitPoint = hitPoint;
        this.hitNormal = hitNormal;
        this.color = color;
        this.isHit = isHit;

    }

    public Hit() {
        this.hitPoint = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, 1.0};
        this.hitNormal = new double[]{0, 0, 0, 0};
        this.color = new double[]{0, 0, 0, 1};
        this.isHit = false;
    }

}
