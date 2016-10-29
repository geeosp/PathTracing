/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.models;

import java.util.ArrayList;

/**
 *
 * @author geeo
 */
public class ObjModel extends Model {

    private  ArrayList<Double[]> vertices;
    private  ArrayList<Double[]> arestas;
    private ArrayList<Double[]> normals;
    @Override
    public Hit getNearestIntersectionPoint(double[] origin, double[] direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ObjModel(double[] position, double[] rotation, double[] scale,ArrayList<Double[]> vertices, ArrayList<Double[]> arestas ) {
        super(position, rotation, scale);
        this.vertices = vertices;
        this.arestas = arestas;
       this.normals =  calculateNormals();
    }
    public ObjModel(double[] position, double[] rotation, double[] scale,ArrayList<Double[]> vertices, ArrayList<Double[]> arestas , ArrayList<Double[]> normals ) {
        super(position, rotation, scale);
        this.vertices = vertices;
        this.arestas = arestas;
        this.normals = normals;
        
    }

  

    public ArrayList<Double[]> calculateNormals() {
     return new  ArrayList<Double[]> (vertices.size());


    }
    
    
    
    
}
