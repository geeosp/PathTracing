/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

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
    private double[][] normals;
    private double[] color;
    private double[] coeficients;

    public ObjModel(String objectName, double[] objectColor) {
        super(objectName, new double[3], new double[3], new double[]{1, 1, 1}, Type.OBJECT);
        ///hrow new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                        arq.readInt(), arq.readInt(), arq.readInt()
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

        //calculateNormals();
        arq.close();

    }

    @Override
    public Hit getNearestIntersectionPoint(double[] origin, double[] direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ObjModel(String name, double[] position, double[] rotation, double[] scale, double[][] vertices, int[][] arestas) {
        super(name, position, rotation, scale, Model.Type.OBJECT);
        this.vertices = vertices;
        this.triangles = arestas;
        this.normals = calculateNormals();
        this.name = name;
    }

    public ObjModel(String name, double[] position, double[] rotation, double[] scale, double[][] vertices, int[][] arestas, double[][] normals) {
        super(name, position, rotation, scale, Model.Type.OBJECT);
        this.vertices = vertices;
        this.triangles = arestas;
        this.normals = normals;

    }

    public double[][] calculateNormals() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void load(String path) {
        Arquivo arq = new Arquivo(path, "lixoObj.txt");
        int Np = arq.readInt();
        int Nt = arq.readInt();
        this.vertices = new double[Np][3];
        this.triangles = new int[Nt][3];

        for (int i = 0; i < Np; i++) {
            for (int j = 0; j < 3; j++) {
                vertices[i][j] = arq.readDouble();
            }
        }
        for (int i = 0; i < Nt; i++) {
            for (int j = 0; j < 3; j++) {
                triangles[i][j] = arq.readInt() - 1;
            }
        }
    }

}
