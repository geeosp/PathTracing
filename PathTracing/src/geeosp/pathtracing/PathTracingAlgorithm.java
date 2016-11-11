/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import geeosp.pathtracing.models.Hit;
import geeosp.pathtracing.models.Model;
import geeosp.pathtracing.models.DifuseModel;
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
        //   System.out.println(n);
        double[] ref = Algeb.normalize(new double[]{1, 1, 1, 0});
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
        i = 0;
        for (double y = 0; y < 2 * PI; y += dy) {
            for (double w = 0; w < PI; w += dw) {
                double a1 = -dw + rand.nextDouble() * dw;
                double a2 = -dy + rand.nextDouble() * dy;
                brdf[i] = Algeb.normalize(Algeb.matrixVectorProduct(rotation(w + a1, y + a2), ref));//Algeb.normalize(ref);
                System.err.println(i);
                i++;

            }
        }
        System.out.println(Algeb.MatrixToString(brdf));

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
        direction = Algeb.sub(onScreen, scene.getEye());
        direction = Algeb.normalize(direction);
        hit = getNextHit(scene.getEye(), direction, scene);
        double[] color = new double[4];
        color[3] = 1;
        if (hit.isHit()) {
            double dot = Algeb.dot(direction, hit.normal);

            switch (hit.model.getType()) {
                case LIGHT:

                    color = ((ObjLight) hit.model).getColor(scene.getEye(), hit.point);
                    

                    break;
                case OBJECT:
                    DifuseModel difuseModel = (DifuseModel) hit.model;
                    for (int r = 0; r < brdf.length; r++) {
                        Random rand = new Random();
                        double ka = ((DifuseModel) hit.model).getCoeficients()[0];
                        double ks = ((DifuseModel) hit.model).getCoeficients()[2];
                        double kd = ((DifuseModel) hit.model).getCoeficients()[1];
                        double ktot = ka + kd + ks;
                        brdf[r] = Algeb.matrixVectorProduct(rotation(rand.nextDouble() * PI * .25, rand.nextDouble() * PI * .25), brdf[r]);//Algeb.normalize(ref);
                        double[] ray = brdf[r];
                        if (Algeb.dot(ray, hit.normal) < 0) {
                            ray = Algeb.dotByScale(-1, ray);
                        }
                        Hit test = getNextHit(hit.point, ray, scene);
                        double random = rand.nextDouble() * (ktot);
                        double[] fator = new double[4];
                        int deep = 4;
                        if (random < ka) {
                            fator = Algeb.dotByScale(ka * scene.getAmbientColor() / brdf.length, hit.color);
                        } else if (random < ka + kd) {
                            fator = Algeb.dotByScale(kd, runAlgorithm(hit.point, test, RayType.DIFUSE, scene, deep));
                        } else if (random < ka + kd + ks) {
                            fator = Algeb.dotByScale(kd, runAlgorithm(hit.point, test, RayType.SPECULAR, scene, deep));

                        }
                        color = Algeb.soma(color, fator);

                    }

                    break;
            }

        } else {
            color = scene.getBackgroundColor();
        }

        color = toneMapping(color,scene);

        return color;
        // return hit.color;

    }

    double[] toneMapping(double[] color, RenderScene scene) {
        double tm = scene.getTonemapping();
        for (int i = 0; i < color.length; i++) {
            color[i] = color[i] / (color[i] + tm);
        }
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

    Hit getNextHit(double[] origin, double[] direction, RenderScene scene
    ) {
        Hit hit = new Hit();
        for (int t = 0; t < scene.getModels().size(); t++) {
            Model a = scene.getModels().get(t);
            Hit temp = a.getNearestIntersectionPoint(origin, direction);
            if (temp.isHit()) {
                double tempDist = Algeb.distance(temp.point, scene.getEye());
                if (tempDist < Algeb.distance(hit.point, scene.getEye())) {
                    hit = temp;
                }
            }
        }
        return hit;
    }

    enum RayType {
        DIFUSE, SPECULAR, TRANSMITED
    };

    double[] runAlgorithm(double[] origin, Hit hit, RayType rayType, RenderScene scene, int deep) {
        double[] color = new double[4];
        double[] normal = hit.normal;
        double[] observer = Algeb.normalize(Algeb.sub(origin, hit.point));
        Model model = hit.model;
        if (Algeb.dot(observer, normal) < 0) {
            normal = Algeb.normalize(Algeb.dotByScale(-1, normal));
        }

        if (hit.isHit()) {
            if (model.isLight()) {
                color = model.getColor(origin, hit.point);
            } else if (deep == 0) {
                color = model.getColor(origin, hit.point);
            } else {
                double[] refect = reflect(origin, hit.normal);
                Hit newHit = getNextHit(hit.point, refect, scene);
                double[] tempCOlor = runAlgorithm(hit.point, newHit, rayType, scene, deep - 1);
                if (hit.isHit()) {
                    switch (rayType) {
                        case DIFUSE:
                            tempCOlor = Algeb.dotByScale(((DifuseModel) model).getKd() * Algeb.dot(normal, refect), tempCOlor);
                            break;
                        case SPECULAR:
                            tempCOlor = Algeb.dotByScale(((DifuseModel) model).getKs() * Algeb.dot(origin, refect), tempCOlor);
                            break;

                    }
                }
                color = tempCOlor;
            }
        }
        return color;
    }
}
