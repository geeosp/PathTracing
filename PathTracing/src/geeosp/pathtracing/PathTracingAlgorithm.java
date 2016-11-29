/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import geeosp.pathtracing.models.Hit;

import geeosp.pathtracing.models.Light;
import geeosp.pathtracing.models.Model;
import geeosp.pathtracing.models.DifuseModel;

import geeosp.pathtracing.scene.RenderScene;
import java.util.Random;

/**
 *
 * @author geeo
 */
public class PathTracingAlgorithm extends RenderAlgorithm {

    final double threshold = .01;
    final double PI = Math.PI;
    double[][] brdf;
    Random rand;

    public PathTracingAlgorithm() {
    }

    public void set(RenderScene scene) {
        rand = new Random(scene.getSeed());
    }

    double[][] rotation(double x, double y) {

        double[][] r1 = new double[][]{
            {1, 0, 0, 0},
            {0, Math.cos(x), -Math.sin(x), 0},
            {0, Math.sin(x), Math.cos(x), 0},
            {0, 0, 0, 1}
        };
        double[][] r2 = new double[][]{
            {Math.cos(y), 0, Math.sin(y), 0},
            {0, 1, 0, 0},
            {-Math.sin(y), 0, Math.cos(y), 0},
            {0, 0, 0, 1}
        };
        return Algeb.matrixMatrixProduct(r1, r2);
    }

    public double[] calulatePixel(int i, int j, RenderScene scene) {
        Hit modelHit;// = new Hit();
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
        direction = Algeb.sub(onScreen, scene.getEye());
        direction = Algeb.normalize(direction);
        //  System.out.println(Algeb.VectorToString(direction));
        modelHit = getNextHit(scene.getEye(), direction, scene);
        double[] color;
        if (modelHit.isHit()) {
            color = new double[4];
            for (int p = 0; p < scene.getNpaths(); p++) {
                double[] pathColor = new double[4];
                if (modelHit.model.isLight()) {
                    pathColor = Algeb.soma(pathColor, ((Light) modelHit.model).getColor());
                } else {//nao é luz
                    DifuseModel model = (DifuseModel) modelHit.model;
                    double kd = model.getKd();
                    double ks = model.getKs();
                    double kt = model.getKt();
                    double ktot = kd + ks + kt;
                    double r = rand.nextDouble() * ktot;
                    for (int l = 0; l < scene.getLights().size(); l++) {
                        Light lg = (Light) scene.getLights().get(l);
                        double[] pl = lg.getOneLightPosition();

                        if (canSee(pl, (Model) lg, modelHit.point, modelHit.model, scene)) {
                            System.err.println("illuminate");
                            pathColor = modelHit.color;
                        }
                    }

                }

                color = Algeb.soma(color, pathColor);
            }

        } else {//hit nothing
            color = scene.getBackgroundColor();
        }

        color = Algeb.dotByScale(1.0 / scene.getNpaths(), color);
      //  color = toneMapping(color, scene.getTonemapping());

        return color;

    }

    double[] toneMapping(double[] color, double tm) {

        for (int i = 0; i < color.length - 1; i++) {
            //color[i] = color[i] / (max);
            color[i] = color[i] / (color[i] + tm);

        }
        color[3] = 1;

        return color;
    }

    double[] reflect(double[] incident, double[] normal
    ) {
        if (Algeb.dot(incident, normal) < 0) {
            incident = Algeb.dotByScale(-1, incident);
        }

        double[] x = Algeb.projection(incident, normal);
        double[] reflect = Algeb.soma(incident, Algeb.dotByScale(-2, Algeb.sub(incident, x)));
        return reflect;

    }

    Hit getNextHit(double[] origin, double[] direction, RenderScene scene) {
        Hit hit = null;
        direction = Algeb.normalize(direction);
        for (int t = 0; t < scene.getModels().size(); t++) {
            Model a = scene.getModels().get(t);
            Hit temp = a.getNearestIntersectionPoint(origin, direction);
            if (temp.isHit()) {
                if (hit == null) {
                    hit = temp;
                }
                double tempDist = Algeb.distance(temp.point, origin);
                if (tempDist < Algeb.distance(hit.point, origin)) {
                    hit = temp;
                }
            }
        }

        return hit;
    }

    boolean canSee(double[] originPoint, Model originModel, double[] targetPoint, Model targetModel, RenderScene scene) {
        Hit hit = null;
        double[] direction = Algeb.sub(targetPoint, originPoint);
        direction = Algeb.normalize(direction);
        for (int t = 0; t < scene.getModels().size(); t++) {
            Model a = scene.getModels().get(t);
            if (!a.getName().equals(originModel.getName())) {
                Hit temp = a.getNearestIntersectionPoint(originPoint, direction);
                if (temp.isHit()) {
                    if (hit == null) {
                        hit = temp;
                    }
                    double tempDist = Algeb.distance(temp.point, originPoint);
                    if (tempDist < Algeb.distance(hit.point, originPoint)) {
                        hit = temp;
                    }
                }
            }
        }

        return (hit.model != null) && hit.model.getName().equals(targetModel.getName());
    }

    enum RayType {
        DIFUSE, SPECULAR, TRANSMITED
    };

    double[] tracePath(double[] origin, Hit hit, RayType rayType, RenderScene scene, int deep) {
        double[] color = new double[4];

        if (deep == 0) {
            if (hit.isHit()) {
                color = hit.model.getColor(origin, hit.point);
            }
        } else if (hit.isHit()) {
            if (hit.model.isLight()) {
                color = hit.model.getColor(origin, hit.point);
            } else {//n é uma luz
                double[] incident = Algeb.normalize(Algeb.sub(origin, hit.point));
                DifuseModel model = (DifuseModel) hit.model;
                if (Algeb.dot(incident, hit.normal) < 0) {
                    incident = Algeb.dotByScale(-1, incident);
                }
                double[] reflectedRay = reflect(incident, hit.normal);
                Hit next = getNextHit(hit.point, reflectedRay, scene);
                switch (rayType) {
                    case DIFUSE:
                        double[] reflectedColor = tracePath(hit.point, next, rayType, scene, deep - 1);
                        double kd = model.getCoeficients()[1];
                        double[] temp = Algeb.dotByScale(kd, Algeb.crossdot(model.getColor(), reflectedColor));
                        color = Algeb.soma(hit.color, temp);
                        break;
                    case SPECULAR:
                        reflectedColor = tracePath(hit.point, next, rayType, scene, deep - 1);
                        double ks = model.getCoeficients()[2];
                        temp = Algeb.dotByScale(ks, reflectedColor);
                        color = Algeb.soma(hit.color, temp);
                        break;
                }
            }
        }

        return color;
    }
}
