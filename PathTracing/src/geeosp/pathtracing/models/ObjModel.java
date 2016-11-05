/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

import geeosp.pathtracing.Algeb;
import geeosp.pathtracing.Arquivo;
import geeosp.pathtracing.scene.Settings;
import java.util.ArrayList;

/**
 *
 * @author geeo
 */
public abstract class ObjModel extends Model {

    protected double[][] vertices;
    protected int[][] triangles;
    protected double[][] normalsVertices;
    protected double[][] normalsTriangle;

    private final double zero = 0.001;

    public ObjModel(String objectName, Model.Type type) {
        super(objectName, new double[3], new double[3], new double[]{1, 1, 1}, type);

       // System.out.println(objectName);
        Arquivo arq = new Arquivo(Settings.modelsFolder + objectName, "dumb.txt");
        ArrayList<double[]> vs = new ArrayList<>();
        ArrayList<int[]> fs = new ArrayList<>();
        while (!arq.isEndOfFile()) {
            String s = arq.readString();
            switch (s) {
                case "v":
                    double[] v = new double[]{
                        arq.readDouble(), arq.readDouble(), arq.readDouble(), 1.0
                    };
                    vs.add(v);
                    break;
                case "f":

                    int[] f = new int[]{
                        arq.readInt() - 1, arq.readInt() - 1, arq.readInt() - 1
                    };
                    fs.add(f);
                    break;

                case "#"://ignore the rest of the line
                    while (!arq.isEndOfLine()) {
                        arq.readString();
                    }
                    break;

                /*
                 */
                default://ignore the rest of the line
                    while (!arq.isEndOfLine()) {
                        arq.readString();
                    }
                    break;
            }

        }
        vertices = new double[vs.size()][4];
        for (int i = 0; i < vs.size(); i++) {
            vertices[i] = vs.get(i);

        }

        triangles = new int[fs.size()][3];
        for (int i = 0; i < fs.size(); i++) {
            triangles[i] = fs.get(i);

        }

        calculateNormals();
        arq.close();

    }

    public void calculateNormals() {
        double[] p1, p2, p3, v1, v2, nt;
        normalsTriangle = new double[triangles.length][4];
        normalsVertices = new double[vertices.length][4];
     //   System.out.println(vertices.length);
       // System.out.println(triangles.length);
        for (int i = 0; i < triangles.length; i++) {
            p1 = vertices[triangles[i][0]];
            p2 = vertices[triangles[i][1]];
            p3 = vertices[triangles[i][2]];

            v1 = Algeb.sub(p2, p1);
            v2 = Algeb.sub(p3, p1);
            nt = Algeb.cross(v1, v2);
            nt = Algeb.normalize(nt);
            normalsTriangle[i] = nt;

            for (int j = 0; j < 3; j++) {
                normalsVertices[triangles[i][j]] = Algeb.soma(nt, normalsVertices[triangles[i][j]]);
            }

        }

        for (int i = 0; i < normalsVertices.length; i++) {
            normalsVertices[i] = Algeb.normalize(normalsVertices[i]);
        }

    }

    @Override
    public Hit getNearestIntersectionPoint(double[] origin, double[] direction) {
        Hit hit = new Hit(new double[4], new double[4], new double[]{1, 1, 1, 0}, false);
        double dist = Double.MAX_VALUE;
        direction = Algeb.normalize(direction);
        for (int t = 0; t < triangles.length; t++) {
            double[] p1, p2, p3, e1, e2, n, p;
            p1 = vertices[triangles[t][0]];
            p2 = vertices[triangles[t][1]];
            p3 = vertices[triangles[t][2]];
            e1 = Algeb.sub(p2, p1);
            e2 = Algeb.sub(p3, p1);
            n = Algeb.cross(e1, e2);
            n = Algeb.normalize(n);
            if (Algeb.dot(n, n) - Algeb.dot(n, direction) > zero) {
                double[] p1p0 = Algeb.sub(origin, p1);
                double s = -Algeb.dot(n, p1p0) / Algeb.dot(n, direction);
                p = Algeb.soma(origin, Algeb.prodByEscalar(s, direction));

                boolean ok = true;

                double[] coeficients = Algeb.barCoef(p, p1, p2, p3);
                for (int i = 0; i < coeficients.length; i++) {
                    if (coeficients[i] < 0 || coeficients[i] > 1) {
                        ok = false;
                    }
                }
                if (ok) {
                    if (dist > Algeb.distanciaSqr(p, origin)) {
                        hit.hitPoint = p;
                        hit.color = getColor(origin, p);
                        hit.hitNormal = normalsTriangle[t];
                        hit.model = this;
                    }
                } else {
                    //   System.out.println("coeficientes baricentricos: " + Algeb.VectorToString(coeficients));

                }

            }
        }
        return hit;
    }

    public abstract double[] getColor(double[] origin, double[] target);

    @Override
    public String toString() {
        String s = super.toString()
                + "\n vertices: " + Algeb.MatrixToString(vertices)
                + "\n triangles: " + Algeb.MatrixToString(triangles);
        return s;

    }
}
