/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.scene;

import geeosp.pathtracing.Arquivo;
import geeosp.pathtracing.models.ObjModel;

/**
 *
 * @author geeo
 */
public class SceneLoader {

    public static RenderScene load() {
        Arquivo arq = new Arquivo(Settings.scenePath, "dumb.txt");
        RenderScene scene = new RenderScene();
        while (!arq.isEndOfFile()) {
            String s = arq.readString();
            switch (s) {//comentario: #qualquercoisa
                case "#":
                    while (!arq.isEndOfLine()) {
                        arq.readString();
                    }
                    break;
                case "output":// output nomedoarquivo
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
                        x, y, z
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
                    int w = arq.readInt();
                    int h = arq.readInt();
                    scene.size = new int[]{w, h};
                    break;
                case "background":
                    double r,
                     g,
                     b;
                    r = arq.readDouble();
                    g = arq.readDouble();
                    b = arq.readDouble();
                    scene.backgroundColor = new double[]{
                        r, g, b
                    };
                    break;
                case "ambient"://ambient la
                    System.out.println("oi");
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

                case "object":
                    String objectName = arq.readString();
                    double[] objectColor = new double[]{
                        arq.readDouble(),//r
                        arq.readDouble(),//g
                        arq.readDouble(),//b
                        arq.readDouble(),//ka
                        arq.readDouble(),//kd
                        arq.readDouble(),//ks
                        arq.readDouble(),//kt
                        arq.readDouble()//refractionindice
                    };
                    scene.models.add(new ObjModel(objectName, objectColor));
                    
                            

                    break;
                /*

                case "light":

                    break;

                case "objectquadric":

                    break;

                 */
                default://ignore the rest of the line
                    while (!arq.isEndOfLine()) {
                        arq.readString();
                    }
                    break;
            }
        }
        arq.close();
        return scene;
    }

}
