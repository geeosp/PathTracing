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
    public double[][] universeWindow;
    public double[] backgroundColor;
    public double ambientColor;
    public ArrayList<Model> models;
    public ArrayList<Model> light;
    public int npaths;
    public double tonemapping;
    public int seed;
    public String outfile;
    double[] monitorWindow;

    public RenderScene() {
        models = new ArrayList<>();
        light = new ArrayList<>();
    }

    @Override
    public String toString() {
        String ret = "";
        ret += outfile.toString() + " \n"
                + "\neye:\n " + Algeb.VectorToString(eye)
                + "\northo:\n " + Algeb.MatrixToString(universeWindow)
                + "\nsize:\n" + Algeb.VectorToString(monitorWindow)
                + "\nbackground:\n" + Algeb.VectorToString(backgroundColor)
                + "\nambient:\n" + " " + ambientColor
                + "\nseed:\n" + " " + seed
                + "\ntonemapping\n" + " " + tonemapping
                + "\nnpaths\n  " + npaths;

        return ret;
    }
}
