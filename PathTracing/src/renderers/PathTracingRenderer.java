/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package renderers;

import geeosp.pathtracing.models.Hit;
import geeosp.pathtracing.models.Model;

import geeosp.pathtracing.models.ObjDifuseModel;
import geeosp.pathtracing.models.ObjLight;
import geeosp.pathtracing.models.ObjModel;
import geeosp.pathtracing.scene.RenderScene;
import java.util.Random;
import javafx.scene.paint.Color;
import geeosp.pathtracing.Algb;

/**
 *
 * @author geeo
 */
public class PathTracingRenderer extends RenderAlgorithm {

    final double PI = Math.PI;
    double[][] brdf;

    public PathTracingRenderer() {
    }

    public int findRightK(int n) {
        int k = 0;
        while (2 * k * k < n) {
            k++;
        }
        return k;
    }

    public void set(RenderScene scene) {
        Random rand = new Random();
        int n = scene.getNpaths();
        int k = findRightK(n);
        n = 2 * k * k;

        double[] ref = Algb.normalize(new double[]{1, 1, 1, 0});
        double dk = k;
        double dw = PI / dk;
        double dy = PI / dk;

        int i = 0;
        for (double y = 0; y < 2 * PI; y += dy) {
            for (double w = 0; w < PI; w += dw) {
                i++;
            }
        }

        brdf = new double[i][4];
        i--;
        for (double y = 0; y < 2 * PI; y += dy) {
            for (double w = 0; w < PI; w += dw) {

                double a1 = 0;//-dw+rand.nextDouble()*dw;
                double a2 = 0;//-dy+ rand.nextDouble() *dy;
                brdf[i] = Algb.normalize(Algb.matrixVectorProduct(rotation(w + a1, y + a2), ref));//Algeb.normalize(ref);
                i--;
            }
        }

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
        return Algb.matrixMatrixProduct(r1, r2);
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
        if (hit.isHit()) {
            for (int p = 0; p < scene.getNpaths(); p++) {
                double[] fator = new double[4];
                if (hit.model.isLight()) {
                    ObjLight lg = (ObjLight) hit.model;

                    fator = lg.getColor();
                } else {//is a object
                    //phong
                    double[] phong = phongBase(hit, scene, scene.getEye());
                    fator = Algb.soma(fator, phong);
                }

                color = Algb.soma(color, fator);
            }
            color = Algb.dotByScale(1.0 / scene.getNpaths(), color);
        } else {
            color = scene.getBackgroundColor();
        }
        color = toneMap(color, scene.getTonemapping());
        return color;
        // return hit.color;

    }

    double[] phongBase(Hit hit, RenderScene scene, double[] eye) {
        double[] color = new double[4];
        Model obj = hit.model;
        double[] ambient = Algb.dotByScale(scene.getAmbientColor() * obj.getMaterial().ka, obj.getColor());
        double[] diffuse = new double[4];
        double[] specular = new double[4];
        
        for (int l = 0; l < scene.getLights().size(); l++) {
            ObjLight lg = (ObjLight) scene.getLights().get(l);
            double[] lgPt = lg.getOnePoint();
            if (canSee(lg, lgPt, hit.model, hit.point, scene)) {
                double[] lgDir = Algb.normalize(Algb.sub(lgPt, hit.point));
                double cos = Math.max(0.0, Algb.dot(lgDir, hit.normal));
                diffuse = Algb.soma(diffuse, Algb.dotByScale(cos * obj.getMaterial().kd, Algb.crossdot(lg.getColor(), hit.model.getColor())));
                //speccular
                double []reflected = reflect(lgDir, hit.normal);
                double []toEye = Algb.normalize(Algb.sub(eye, hit.point));
                cos = Math.max(0, Algb.dot(toEye, reflected));
                specular = Algb.soma(specular, Algb.dotByScale(Math.pow(cos, hit.model.getMaterial().n) * obj.getMaterial().ks, Algb.crossdot(lg.getColor(), hit.model.getColor())));
            }
        }
        color = Algb.soma(color, ambient);
        color = Algb.soma(color, diffuse);
        color = Algb.soma(color, specular);
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

    enum RayType {
        DIFUSE, SPECULAR, TRANSMITED
    };

    double[] tracePath(double[] origin, Hit hit, RayType rayType, RenderScene scene, int deep) {
        double[] color = scene.getBackgroundColor();
        if (deep == 0) {
            if (hit.isHit()) {
                color = hit.model.getColor();
            } else {
                color = new double[]{scene.getAmbientColor(), scene.getAmbientColor(), scene.getAmbientColor(),
                    (scene.getAmbientColor() == 0) ? 0 : 1};
            }
        } else if (hit.isHit()) {
            if (hit.model.getType() == Model.Type.LIGHT) {
                color = hit.model.getColor();
            } else {//n é uma luz
                double[] incident = Algb.normalize(Algb.sub(origin, hit.point));
                Model model = hit.model;
                if (Algb.dot(incident, hit.normal) < 0) {
                    incident = Algb.dotByScale(-1, incident);
                }
                double[] reflectedRay = reflect(incident, hit.normal);
                //  reflectedRay = new double[]{0,1,0,0};
                Hit next = getNextHit(hit.point, reflectedRay, scene);
                switch (rayType) {
                    case DIFUSE:

                        //    color = hit.color;
                        double[] reflectedColor = tracePath(hit.point, next, rayType, scene, deep - 1);
                        double kd = model.getMaterial().kd;
                        double[] temp = Algb.dotByScale(kd, reflectedColor);
                        //color = Algeb.soma(Algeb.dotByScale(kd, color), temp);
                        color = Algb.soma(hit.color, temp);
                        break;
                    case SPECULAR:
                        reflectedColor = tracePath(hit.point, next, rayType, scene, deep - 1);
                        double ks = model.getMaterial().ks;
                        temp = Algb.dotByScale(ks, reflectedColor);
                        //color = Algeb.soma(Algeb.dotByScale(kd, color), temp);
                        color = Algb.soma(hit.color, temp);
                        break;
                }
            }
        } else {//hit nothing
            color = scene.getBackgroundColor();
        }

        return color;
    }

}
