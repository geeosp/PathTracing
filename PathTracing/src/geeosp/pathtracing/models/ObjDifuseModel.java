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
public class ObjDifuseModel extends ObjModel {

    //private double[] color;
    private Material material;

    public double[] getColor() {
        return material.color;
    }

    public void setColor(double[] color) {
        this.material.color = color;
    }

    public ObjDifuseModel(String objectName, double[] objectMaterial) {
        super(objectName, Type.OBJECT);
        this.material = new Material(
                new double[]{objectMaterial[0], objectMaterial[1], objectMaterial[2], 1.0},
                objectMaterial[3],
                objectMaterial[4],
                objectMaterial[5],
                objectMaterial[6],
                objectMaterial[7]
        );

    }

    @Override
    public String toString() {
        String s = super.toString()
                + "\n color: " + material.color[0] + " " + material.color[1] + " " + material.color[2] + " " + material.color[3]
                + "\n coeficients: " + material.ka + " " + material.kd + " " + material.ks + " " + material.kt + " " + material.n + " ";
        return s;

    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

}
