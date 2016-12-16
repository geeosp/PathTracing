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
public class PathTracingAlgorithm extends RenderAlgorithm {

    final double PI = Math.PI;
    double[][] brdf;

    public PathTracingAlgorithm() {
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
        color[3] = 1;
        if (hit.isHit()) {
            double dot = Algb.dot(direction, hit.normal);

            switch (hit.model.getType()) {
                case LIGHT:

                    color = ((ObjLight) hit.model).getColor();
                    color[3] = 1;

                    break;
                case OBJECT:
             Model difuseModel = hit.model;
                    for (int r = 0; r < brdf.length; r++) {
                        Random rand = new Random();
                        double ka = hit.model.getMaterial().ka;
                        double kd =  hit.model.getMaterial().kd;
                        double ks =  hit.model.getMaterial().ks;
                        double ktot = ka + kd + ks;
                        double[] ray = brdf[r];
                        if (Algb.dot(ray, hit.normal) < 0) {
                            ray = Algb.dotByScale(-1, ray);
                        }
                        Hit test = getNextHit(hit.point, ray, scene);
                        double random = rand.nextDouble() * (ktot - ks);
                        double[] fator = new double[4];
                        if (random < ka) {
                            fator = Algb.dotByScale(ka * scene.getAmbientColor(), hit.color);
                        } else if (random < ka + kd) {
                            fator = Algb.dotByScale(kd , runAlgorithm(hit.point, test, RayType.DIFUSE, scene, 5));
                        } else if (random < ka + kd + ks) {
                            //    fator = Algeb.dotByScale(kd/ brdf.length, runAlgorithm(hit.point, test, RayType.SPECULAR, scene, 5));

                        }
                        color = Algb.soma(color, fator);

                    }

                    break;
            }

        } else {
            color = scene.getBackgroundColor();
        }
        return color;
        // return hit.color;

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

    double[] runAlgorithm(double[] origin, Hit hit, RayType rayType, RenderScene scene, int deep) {
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
      Model model =  hit.model;
                if (Algb.dot(incident, hit.normal) < 0) {
                    incident = Algb.dotByScale(-1, incident);
                }
                double[] reflectedRay = reflect(incident, hit.normal);
                //  reflectedRay = new double[]{0,1,0,0};
                Hit next = getNextHit(hit.point, reflectedRay, scene);
                switch (rayType) {
                    case DIFUSE:

                        //    color = hit.color;
                        double[] reflectedColor = runAlgorithm(hit.point, next, rayType, scene, deep - 1);
                        double kd = model.getMaterial().kd;
                        double[] temp = Algb.dotByScale(kd, reflectedColor);
                        //color = Algeb.soma(Algeb.dotByScale(kd, color), temp);
                        color = Algb.soma(hit.color, temp);
                        break;
                    case SPECULAR:
                        reflectedColor = runAlgorithm(hit.point, next, rayType, scene, deep - 1);
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
