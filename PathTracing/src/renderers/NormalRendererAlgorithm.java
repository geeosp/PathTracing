/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package renderers;

import geeosp.pathtracing.Algb;
import geeosp.pathtracing.models.Hit;


import geeosp.pathtracing.models.Model;

import geeosp.pathtracing.models.ObjDifuseModel;
import geeosp.pathtracing.models.ObjLight;
import geeosp.pathtracing.models.ObjModel;
import geeosp.pathtracing.scene.RenderScene;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 *
 * @author geeo
 */
public class NormalRendererAlgorithm extends RenderAlgorithm {

    final double PI = Math.PI;
    double[][] brdf;

    public NormalRendererAlgorithm() {
    }

   
    public void set(RenderScene scene) {
      
    }


    public double[] calulatePixel(int i, int j, RenderScene scene) {
        Hit hit = new Hit();
        double[] direction = new double[4];
        double x0 = scene.getOrtho()[0][0];
        double x1 = scene.getOrtho()[1][0];
        double y0 = scene.getOrtho()[0][0];
        double y1 = scene.getOrtho()[1][0];
        double deltax = (x1 - x0) / scene.getSizeWidth();
        double deltay = (y1 - y0) / scene.getSizeHeight();
        //spacial position of the pixel

        double[] onScreen = new double[]{
            x0 + (i + 1) * deltax,
            y0 + (j + 1) * deltay,
            0,
            1
        };
        direction = Algb.sub(onScreen, scene.getEye());
        direction = Algb.normalize(direction);
        hit = getNextHit(scene.getEye(), direction, scene);
        double[] color = new double[4];
        color[3] = 1;
        if (hit.isHit()) {
            color = Algb.soma(Algb.dotByScale(.5, hit.normal), new double[]{.5, .5, .5, 0});
        } else {
            color = scene.getBackgroundColor();
        }
       // color = toneMapping(color, scene);
        color[3] =1;
        return color;
        //return hit.color;

    }

    double[] toneMapping(double[] color, RenderScene scene) {
        double tm = scene.getTonemapping();
        for (int i = 0; i < color.length; i++) {
            color[i] = color[i] / (color[i] + tm);
        }
        color[3] = 1.0;
        return color;
    }

    double[] reflect(double[] incident, double[] normal
    ) {
        if (Algb.dot(incident, normal) < 0) {
            incident = Algb.dotByScale(-1, incident);
        }

        double[] x = Algb.projection(incident, normal);
        double[] reflect = Algb.soma(incident, Algb.dotByScale(-2, Algb.sub(incident, x)));
        return reflect;

    }

 
    
 
}
