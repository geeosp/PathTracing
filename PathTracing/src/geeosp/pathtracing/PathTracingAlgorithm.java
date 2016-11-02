/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import geeosp.pathtracing.models.Hit;
import geeosp.pathtracing.models.Model;
import geeosp.pathtracing.scene.RenderScene;

/**
 *
 * @author geeo
 */
public class PathTracingAlgorithm extends RenderAlgorithm {

    public PathTracingAlgorithm() {
    }

    @Override
    public double[] calulatePixel(int i, int j, RenderScene scene) {
        Hit hit = new Hit();
        double[] direction = new double[4];
        double x0 = scene.getOrtho()[0][0];
        double x1 = scene.getOrtho()[1][0];
        double y0 = scene.getOrtho()[0][0];
        double y1 = scene.getOrtho()[1][0];
        double deltax = (x1 - x0) / scene.getSizeWidth();
        double deltay = (y1 - y0) / scene.getSizeHeight();

        double[] onScreen = new double[]{
            x0 + (i + 1) * deltax,
            y0 + (j + 1) * deltay,
            0,
            1
        };
        direction = Algeb.sub(onScreen, scene.getEye());
        direction = Algeb.normalize(direction);
        /*calculate direction
        
        
        
         */
        for (int t = 0; t < scene.getModels().size(); t++) {
            Model a = scene.getModels().get(t);
            Hit temp = a.getNearestIntersectionPoint(scene.getEye(), direction);
            if (temp.isHit) {
                double tempDist = Algeb.distancia(temp.hitPoint, scene.getEye());
                if (tempDist < Algeb.distancia(hit.hitPoint, scene.getEye())) {
                    hit = temp;
                }
            }
        }

        return hit.color;
    }

}
