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
    protected String name = "";

    public enum Type {
        LIGHT,
        OBJECT
    }

    public abstract double[] getColor();

    protected Type type;

    public abstract Hit getNearestIntersectionPoint(double[] origin, double[] direction);

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

    public Model(String name, double[] position, double[] rotation, double[] scale, Type type) {
        this.name = name;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isLight() {
        return this.type == Type.LIGHT;
    }

    public abstract Material getMaterial();

}
