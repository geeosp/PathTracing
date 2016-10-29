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


public abstract class Model {
    
    
protected double[] position;
protected double[] rotation;
protected double[] scale;
public abstract Hit getNearestIntersectionPoint(double[] origin, double [] direction);

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public double[] getRotation() {
        return rotation;
    }

    public void setRotation(double[] rotation) {
        this.rotation = rotation;
    }

    public double[] getScale() {
        return scale;
    }

    public void setScale(double[] scale) {
        this.scale = scale;
    }

    public Model(double[] position, double[] rotation, double[] scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
    
    
     
    
    
}
