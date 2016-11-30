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

    protected final double zero = .001;

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

    boolean canSee(Model lg, double[] lgPt, Model model, double[] point, RenderScene scene) {
        double[] dir = Algb.normalize(Algb.sub(point, lgPt));
        double minDist = Double.MAX_VALUE;
        double modelDist = Double.MAX_VALUE;
        double[] minPoint = new double[4];
        for (int i = 0; i < scene.getModels().size(); i++) {
            Model t = scene.getModels().get(i);
            if (!t.getName().equals(lg.getName())) { //ignora a luz
                Hit h = t.getNearestIntersectionPoint(lgPt, dir);
                if (h.isHit()) {
                    double dist = Algb.distance(lgPt, h.point);
                    if (dist < minDist) {
                        minDist = dist;
                        minPoint = h.point;
                    }
                    if (h.model.getName().equals(model.getName())) {
                        modelDist = dist;
                    }

                }
            }
        }

        return (modelDist == minDist) && Algb.distance(minPoint, point) <= zero;
    }

    double[] toneMap(double[] color, double tonemap) {
        double[] ret = new double[color.length];
        for (int i = 0; i < 3; i++) {
            ret[i] = color[i] / (color[i] + tonemap);
        }
        ret[3] = 1;
        return ret;
    }
}
