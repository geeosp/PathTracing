/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.scene;

import geeosp.pathtracing.Algb;
import geeosp.pathtracing.Arquivo;
import geeosp.pathtracing.models.*;

import java.util.ArrayList;

/**
 * @author geeo
 */
public class RenderScene {

    private double[] eye;
    private double[][] ortho;
    private double[] backgroundColor;
    private double ambientColor;
    private ArrayList<Model> models;
    private ArrayList<Model> lights;
    private int npaths;
    private double tonemapping;
    private int seed;
    private String outfile;
    private int[] size;
    private int nthreads;
    private int rayDepth;
    private double baseN;

    public RenderScene() {
        models = new ArrayList<>();
        lights = new ArrayList<>();
        // nthreads = 1;
        npaths = 100;
        eye = new double[]{0, 0, -1};
        rayDepth = 5;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += outfile.toString() + " \n"
                + "\neye:\n " + Algb.VectorToString(eye)
                + "\northo:\n " + Algb.MatrixToString(ortho)
                + "\nsize:\n" + Algb.VectorToString(size)
                + "\nbackground:\n" + Algb.VectorToString(backgroundColor)
                + "\nambient:\n" + " " + ambientColor
                + "\nseed:\n" + " " + seed
                + "\ntonemapping\n" + " " + tonemapping
                + "\nnpaths\n  " + npaths
                + "\nrayDepth\n  " + rayDepth
                + "\nthreads\n  " + nthreads
                + "\n";
        for (int i = 0; i < models.size(); i++) {
            ret += "\n" + models.get(i);
        }
        return ret;
    }

    public double[] getEye() {
        return eye;
    }

    public double[][] getOrtho() {
        return ortho;
    }

    public double[] getBackgroundColor() {
        return backgroundColor;
    }

    public int getRayDepth() {
        return rayDepth;
    }

    public void setRayDepth(int rayDepth) {
        this.rayDepth = rayDepth;
    }

    public double getAmbientColor() {
        return ambientColor;
    }

    public ArrayList<Model> getModels() {
        return models;
    }

    public ArrayList<Model> getLights() {
        return lights;
    }

    public int getNpaths() {
        return npaths;
    }

    public double getTonemapping() {
        return tonemapping;
    }

    public int getSeed() {
        return seed;
    }

    public String getOutfile() {
        return outfile;
    }

    public int[] getSize() {
        return size;
    }

    public int getSizeWidth() {
        return size[0];
    }

    public int getSizeHeight() {
        return size[1];
    }

    public int getNthreads() {
        return nthreads;
    }

    public static RenderScene load() {
        Arquivo arq = new Arquivo(Settings.scenePath, "dumb.txt");
        RenderScene scene = new RenderScene();
        while (!arq.isEndOfFile()) {
            String s = arq.readString();
            switch (s) {// comment: #anything
                case "#":
                    while (!arq.isEndOfLine()) {
                        arq.readString();
                    }
                    break;
                case "output":// output filename
                    scene.outfile = arq.readString();
                    while (!arq.isEndOfLine()) {
                        scene.outfile += arq.readString();
                    }
                    break;
                case "nthreads":
                    scene.nthreads = arq.readInt();
                    break;

                case "eye":
                    double x,
                            y,
                            z;
                    x = arq.readDouble();
                    y = arq.readDouble();
                    z = arq.readDouble();
                    assert (z > 0);
                    scene.eye = new double[]{
                            x, y, z, 1.0
                    };
                    break;
                case "ortho":
                    double x0,
                            y0,
                            x1,
                            y1;
                    x0 = arq.readDouble();
                    y0 = arq.readDouble();
                    x1 = arq.readDouble();
                    y1 = arq.readDouble();
                    scene.ortho = new double[][]{
                            {x0, y0}, {x1, y1}
                    };
                    break;
                case "size":
                    int width = arq.readInt();
                    int height = arq.readInt();
                    scene.size = new int[]{width, height};
                    break;
                case "background":
                    double r,
                            g,
                            b;
                    r = arq.readDouble();
                    g = arq.readDouble();
                    b = arq.readDouble();
                    scene.backgroundColor = new double[]{
                            r, g, b, 1
                    };
                    break;
                case "ambient"://ambient la
                    scene.ambientColor = arq.readDouble();
                    break;
                case "seed":
                    scene.seed = arq.readInt();
                    break;
                case "tonemapping":
                    scene.tonemapping = arq.readDouble();
                    break;
                case "npaths":
                    scene.npaths = arq.readInt();
                    break;

                case "raydepht":
                    scene.npaths = arq.readInt();
                    break;
                case "sphere":
                    double center[] = new double[]{
                            arq.readDouble(),
                            arq.readDouble(),
                            arq.readDouble(),
                            1

                    };
                    double radius = arq.readDouble();
                    double[] sphereMaterial = new double[]{
                            arq.readDouble(),//r
                            arq.readDouble(),//g
                            arq.readDouble(),//b
                            arq.readDouble(),//ka
                            arq.readDouble(),//kd
                            arq.readDouble(),//ks
                            arq.readDouble(),//kt
                            arq.readDouble(),//q
                            arq.readDouble()//refractionindice
                    };
                    scene.models.add(new SphereModel(center, radius, sphereMaterial));

                    break;
                case "object":
                    String objectName = arq.readString();
                    double[] objectMaterial = new double[]{
                            arq.readDouble(),//r
                            arq.readDouble(),//g
                            arq.readDouble(),//b
                            arq.readDouble(),//ka
                            arq.readDouble(),//kd
                            arq.readDouble(),//ks
                            arq.readDouble(),//kt
                            arq.readDouble(),//q
                            arq.readDouble()//refractionindice
                    };
                    scene.models.add(new ObjDifuseModel(objectName, objectMaterial));

                    break;
                case "basen":
                    scene.baseN = arq.readDouble();
                    break;
                case "light":
                    objectName = arq.readString();
                    objectMaterial = new double[]{
                            arq.readDouble(),//r
                            arq.readDouble(),//g
                            arq.readDouble(),//b
                            arq.readDouble(),//Intensity
                    };
                    ObjLight light = new ObjLight(objectName, objectMaterial);
                    scene.models.add(light);
                    scene.lights.add(light);
                    break;
                case "config":
                    scene.rayDepth = arq.readInt();

                    break;

                case "objectquadric":
                    double a, c, d, e, f, h, i, j;
                    double minx, miny, minz, maxx, maxy, maxz;
                    a = arq.readDouble();
                    b = arq.readDouble();
                    c = arq.readDouble();
                    d = arq.readDouble();
                    e = arq.readDouble();
                    f = arq.readDouble();
                    g = arq.readDouble();
                    h = arq.readDouble();
                    i = arq.readDouble();
                    j = arq.readDouble();
                    minx = arq.readDouble();
                    maxx = arq.readDouble();
                    miny = arq.readDouble();
                    maxy = arq.readDouble();
                    minz = arq.readDouble();
                    maxz = arq.readDouble();


                    objectMaterial = new double[]{
                            arq.readDouble(),//r
                            arq.readDouble(),//g
                            arq.readDouble(),//b
                            arq.readDouble(),//ka
                            arq.readDouble(),//kd
                            arq.readDouble(),//ks
                            arq.readDouble(),//kt
                            arq.readDouble(),//q
                            arq.readDouble()//refractionindice
                    };
                    QuadricModel quadric = new QuadricModel("quadric " + scene.models.size(), a, b, c, d, e, f, g, h, i, j, minx, maxx, miny, maxy, minz, maxz, objectMaterial);
                    scene.models.add(quadric);
                    System.err.println(quadric);
                    break;

                default://ignore the rest of the line
                    while (!arq.isEndOfLine()) {
                        arq.readString();
                    }
                    break;
            }
        }
        arq.close();
//System.out.print(scene);
        return scene;
    }

    public double getBaseN() {
        return baseN;
    }
}
