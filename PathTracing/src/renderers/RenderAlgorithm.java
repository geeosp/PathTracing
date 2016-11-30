/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package renderers;

import geeosp.pathtracing.Algb;
import geeosp.pathtracing.models.Hit;
import geeosp.pathtracing.models.Model;
import geeosp.pathtracing.scene.RenderScene;

/**
 *
 * @author geeo
 */
public abstract class RenderAlgorithm {

    public abstract void set(RenderScene renderScene);

    public abstract double[] calulatePixel(int i, int y, RenderScene scene);
 Hit getNextHit(double[] origin, double[] direction, RenderScene scene
    ) {
        Hit hit = new Hit();
        for (int t = 0; t < scene.getModels().size(); t++) {
            Model a = scene.getModels().get(t);
            Hit temp = a.getNearestIntersectionPoint(origin, direction);
            if (temp.isHit()) {
                double tempDist = Algb.distance(temp.point, scene.getEye());
                if (tempDist < Algb.distance(hit.point, scene.getEye())) {
                    hit = temp;
                }
            }
        }
        return hit;
    }
}
