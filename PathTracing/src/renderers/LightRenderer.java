/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package renderers;

import geeosp.pathtracing.Algb;
import geeosp.pathtracing.models.Hit;
import geeosp.pathtracing.models.Model;
import geeosp.pathtracing.models.ObjLight;
import geeosp.pathtracing.scene.RenderScene;

/**
 *
 * @author geeo
 */

public class LightRenderer extends RenderAlgorithm {
final double zero =.001;

    RenderScene scene;

    public LightRenderer() {
    }

    @Override
    public void set(RenderScene renderScene) {
        this.scene = renderScene;
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
        // color[3] = 1;
        if (hit.isHit()) {
            if (hit.model.getType() == Model.Type.LIGHT) {
                color = new double[]{1, 1, 0, 1};
            } else {
                for (int l = 0; l < scene.getLights().size(); l++) {
                    double tmp[] = new double[4];
                    int k = scene.getNpaths();
                    double strength = 4.0;
                    for (int s = 0; s < k; s++) {

                        ObjLight lg = (ObjLight) scene.getLights().get(l);
                        double[] lgPt = lg.getOnePoint();
                        if (canSee(lg, lgPt, hit.model, hit.point)) {
                            tmp = Algb.soma(tmp, Algb.dotByScale(strength / (k * Algb.distance(hit.point, lgPt)), new double[]{1, 0, 0, 1}));
                        }
                    }
                    color = Algb.soma(color, tmp);
                }

            }

        } else {
            color = scene.getBackgroundColor();
        }
        // color = toneMapping(color, scene);
        color[3] = 1;
        return color;
        //return hit.color;
    }

    boolean canSee(Model lg, double[] lgPt, Model model, double[] point) {
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

        return (modelDist == minDist)&&Algb.distance(minPoint, point)<=zero;
    }
}
