/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.scene;

import geeosp.pathtracing.Algeb;
import geeosp.pathtracing.models.Model;
import java.util.ArrayList;

/**
 *
 * @author geeo
 */
public class RenderScene {

    public double[] eye;
    public double[][] ortho;
    public double[] backgroundColor;
    public double ambientColor;
    public ArrayList<Model> models;
    public ArrayList<Model> light;
    public int npaths;
    public double tonemapping;
    public int seed;
    public String outfile;
    public int[] size;
    public int nthreads;

    public RenderScene() {
        models = new ArrayList<>();
        light = new ArrayList<>();
       // nthreads = 1;
        npaths = 100;
        eye = new double[]{0, 0, -1};
    }

    @Override
    public String toString() {
        String ret = "";
        ret += outfile.toString() + " \n"
                + "\neye:\n " + Algeb.VectorToString(eye)
                + "\northo:\n " + Algeb.MatrixToString(ortho)
                + "\nsize:\n" + Algeb.VectorToString(size)
                + "\nbackground:\n" + Algeb.VectorToString(backgroundColor)
                + "\nambient:\n" + " " + ambientColor
                + "\nseed:\n" + " " + seed
                + "\ntonemapping\n" + " " + tonemapping
                + "\nnpaths\n  " + npaths
                + "\nthreads\n  " + nthreads;

        return ret;
    }
}
