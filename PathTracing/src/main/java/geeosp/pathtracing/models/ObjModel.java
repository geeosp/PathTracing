/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

import geeosp.pathtracing.Algb;
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

            v1 = Algb.sub(p2, p1);
            v2 = Algb.sub(p3, p1);
            nt = Algb.cross(v1, v2);
            nt = Algb.normalize(nt);
            normalsTriangle[i] = nt;

            for (int j = 0; j < 3; j++) {
                normalsVertices[triangles[i][j]] = Algb.add(nt, normalsVertices[triangles[i][j]]);
            }

        }

        for (int i = 0; i < normalsVertices.length; i++) {
            normalsVertices[i] = Algb.normalize(normalsVertices[i]);
        }

    }

    @Override

    public Hit getNearestIntersectionPoint(double[] origin, double[] direction) {
        Hit hit = new Hit(new double[4], new double[4], new double[]{1, 1, 1, 0}, null);
        double minDist = Double.MAX_VALUE;
        direction = Algb.normalize(direction);
        for (int t = 0; t < triangles.length; t++) {
            double[] p1, p2, p3, e1, e2, n, p;
            p1 = vertices[triangles[t][0]];
            p2 = vertices[triangles[t][1]];
            p3 = vertices[triangles[t][2]];
            e1 = Algb.sub(p2, p1);
            e2 = Algb.sub(p3, p1);
            n = Algb.cross(e1, e2);
            n = Algb.normalize(n);
            if (Algb.dot(n, n) - Algb.dot(n, direction) > zeroCos) {
                double[] p1p0 = Algb.sub(origin, p1);
                double s = -Algb.dot(n, p1p0) / Algb.dot(n, direction);
                if (s > zeroDist) {
                    p = Algb.add(origin, Algb.dotByScale(s, direction));

                    boolean ok = true;

                    double[] coeficients = Algb.barCoef(p, p1, p2, p3);
                    for (int i = 0; i < coeficients.length; i++) {
                        if (coeficients[i] < 0 || coeficients[i] > 1) {
                            ok = false;
                        }
                    }
                    if (ok) {
                        double distance = Algb.distance(p, origin);
                        if (distance < minDist) {
                            hit.point = p;
                            hit.color = getColor();
                            minDist = distance;

                            hit.normal = n;
                            if (Algb.dot(hit.normal, Algb.sub(origin, hit.point)) < 0) {
                                hit.normal = Algb.dotByScale(-1, hit.normal);
                            }

                            hit.model = this;
                        }
                    } else {
                        //   System.out.println("coeficientes baricentricos: " + Algeb.VectorToString(coeficients));

                    }
                }

            }
        }
        return hit;
    }

    @Override
    public String toString() {
        String s = super.toString()
                + "\n vertices: " + Algb.MatrixToString(vertices)
                + "\n triangles: " + Algb.MatrixToString(triangles);
        return s;

    }
    
}
