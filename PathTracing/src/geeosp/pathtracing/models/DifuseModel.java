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
public interface DifuseModel {

    public double[] getCoeficients();
    public double getKa();
    public double getKt();
    public double getKd();
    public double getKs();
        public double[] getColor();
    
    
}
