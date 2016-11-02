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
    public double[] calulatePixel(int i, int y, RenderScene scene) {
        Hit hit = new Hit();
        double[] direction = new double[4];
        /*calculate direction
        
        
        
         */

        for (int t = 0; t < scene.getModels().size(); t++) {
            Model a = scene.getModels().get(t);
            Hit temp = a.getNearestIntersectionPoint(scene.getEye(), direction);
            if (temp.isHit) {
                double tempDist = Algeb.distanciaSqr(temp.hitPoint, scene.getEye());
                if (tempDist < Algeb.distanciaSqr(hit.hitPoint, scene.getEye())) {
                    hit = temp;
                }
            }
        }

        return hit.color;
    }

}
