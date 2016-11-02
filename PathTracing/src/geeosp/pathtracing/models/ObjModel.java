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
public class ObjModel extends Model {

    private double[][] vertices;
    private int[][] triangles;
    private double[][] normalsVertices;
    private double[][] normalsTriangle;
    private double[] color;
    private double[] coeficients;

    public ObjModel(String objectName, double[] objectMaterial) {
        super(objectName, new double[3], new double[3], new double[]{1, 1, 1}, Type.OBJECT);
        this.color = new double[]{objectMaterial[0], objectMaterial[1], objectMaterial[2], 1.0};
        this.coeficients = new double[]{
            objectMaterial[3],
            objectMaterial[4],
            objectMaterial[5],
            objectMaterial[6],
            objectMaterial[7]

        };
        System.out.println(objectName);
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
        System.out.println(vertices.length);
        System.out.println(triangles.length);
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
        double hitDistanceSqr = Double.POSITIVE_INFINITY;
        Hit hit = new Hit(new double[4], new double[4], color, false);
        double t;

        direction = Algeb.normalize(direction);
        double[] p, p1, p2, p3, v1, v2, n;
        for (int i = 0; i < triangles.length; i++) {
            p1 = vertices[triangles[i][0]];
            p2 = vertices[triangles[i][1]];
            p3 = vertices[triangles[i][2]];
            v1 = Algeb.sub(p2, p1);
            v2 = Algeb.sub(p3, p1);
            n = Algeb.normalize(Algeb.cross(v1, v2));

            if (Algeb.dot(direction, direction) != Algeb.dot(direction, n)) {//desconsidera se o plano for paralelo a reta

                try {
                    t = (Algeb.dot(n, p1) - Algeb.dot(n, origin)) / Algeb.dot(n, direction);
                    p = Algeb.soma(origin, Algeb.prodByEscalar(t, direction));
                    double[] coef = Algeb.barCoef(p, p1, p2, p3);
                    boolean ok = true;
                   for (int k = 0; k < 3; k++) {//verify if the point belongs to the triangle
                     if (coef[k] < 0 || coef[k] > 1) {
                           ok = false;
                        }
                    }
            
                    if (ok) {
                        double d = Algeb.distanciaSqr(p, origin);
                        if (d < hitDistanceSqr) {
                            hitDistanceSqr = d;
                            hit.hitPoint = p;
                            hit.hitNormal = n;
                            hit.isHit = true;
                        }
                    }

                } catch (ArithmeticException e) {//divisao by zero
                    System.out.println("geeosp.pathtracing.models.ObjModel.getNearestIntersectionPoint(): DivisionByZero");
                }
            }

        }
        return hit;
    }

}
