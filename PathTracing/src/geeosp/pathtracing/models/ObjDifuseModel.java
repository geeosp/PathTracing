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
public class ObjDifuseModel extends ObjModel implements DifuseModel{

    private double[] color;
    private double[] coeficients;

    public double[] getColor() {
        return color;
    }

    public void setColor(double[] color) {
        this.color = color;
    }

    public double[] getCoeficients() {
        return coeficients;
    }

    public void setCoeficients(double[] coeficients) {
        this.coeficients = coeficients;
    }

    public ObjDifuseModel(String objectName, double[] objectMaterial) {
        super(objectName, Type.OBJECT);
        this.color = new double[]{objectMaterial[0], objectMaterial[1], objectMaterial[2], 1.0};
        this.coeficients = new double[]{
            objectMaterial[3],
            objectMaterial[4],
            objectMaterial[5],
            objectMaterial[6],
            objectMaterial[7]

        };

    }

   
    @Override
    public String toString() {
        String s = super.toString()
                + "\n color: " + color[0] + " " + color[1] + " " + color[2] + " " + color[3]
                + "\n coeficients: " + coeficients[0] + " " + coeficients[1] + " " + coeficients[2] + " " + coeficients[3] + " " + coeficients[4] + " ";
        return s;

    }

}
