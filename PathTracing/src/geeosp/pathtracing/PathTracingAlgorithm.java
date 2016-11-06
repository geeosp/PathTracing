/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import geeosp.pathtracing.models.Hit;
import geeosp.pathtracing.models.Model;
import geeosp.pathtracing.models.DifuseModel;
import geeosp.pathtracing.models.ObjLight;
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
        brdf = new double[n + 1][4];
        double[] ref = Algeb.normalize(new double[]{1, 1, 1, 0});
        System.out.println(Algeb.VectorToString(ref));
        double dw = PI / k;
        double dy = PI / k;

        int i = 0;
        for (double w = dw; w < 2 * PI; w += dw) {
            for (double y = dy; y < PI; y += dy) {
                double a1 = rand.nextDouble() / n;
                double a2 = rand.nextDouble() / n;
                brdf[i] = Algeb.normalize(Algeb.matrixVectorProduct(rotation(w + a1, y + a2), ref));//Algeb.normalize(ref);
                //    System.err.println(Algeb.MatrixToString(rotation(w+a1, y+a2)));
                System.out.println(Algeb.VectorToString(brdf[i]));
                i++;
            }
        }

    }

    double[][] rotation(double u, double v) {

        double[][] r1 = new double[][]{
            {Math.cos(v), Math.sin(v), 0, 0},
            {-Math.sin(v), Math.cos(v), 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
        };
        double[][] r2 = new double[][]{
            {1, 0, 0, 0},
            {0, Math.cos(u), Math.sin(u), 0},
            {0, -Math.sin(u), Math.cos(u), 0},
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
            switch (hit.model.getType()) {
                case LIGHT:
                    double dot = Algeb.dot(direction, hit.normal);
                    if (dot < 0) {
                        dot = -dot;
                    }
                    color = Algeb.dotByScale(dot, ((ObjLight) hit.model).getColor(scene.getEye(), hit.point));
                    break;
                case OBJECT:
                    DifuseModel difuseModel = (DifuseModel) hit.model;
                    for (int r = 0; r < brdf.length; r++) {
                        Random rand = new Random();
                        double ka = ((DifuseModel) hit.model).getCoeficients()[0];
                        double kd = ((DifuseModel) hit.model).getCoeficients()[1];
                        double ks = ((DifuseModel) hit.model).getCoeficients()[2];
                        double ktot = ka + kd + ks;
                        double random = rand.nextDouble() * ktot;
                        double[] fator = new double[4];
                        if (random < ka) {
                            fator = Algeb.dotByScale(ka, Algeb.dotByScale(scene.getAmbientColor() / brdf.length, hit.color));
                        }
                        color = Algeb.soma(color, fator);

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
        double[] x = Algeb.projection(incident, normal);
        double[] reflect = Algeb.soma(incident, Algeb.dotByScale(-2, x));
        return reflect;

    }

    Hit getNextHit(double[] origin, double[] direction, RenderScene scene
    ) {
        Hit hit = new Hit();
        for (int t = 0; t < scene.getModels().size(); t++) {
            Model a = scene.getModels().get(t);
            Hit temp = a.getNearestIntersectionPoint(scene.getEye(), direction);
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

    double[] runAlgorithm(double[] origin, Hit hit, RayType rayType, int deep, boolean firstHit) {
        double[] color = new double[4];
        //vector from hitpoint to origin 
        double[] incident = Algeb.normalize(Algeb.sub(origin, hit.point));
        double dotProduct = Algeb.dot(incident, hit.normal);
        if (dotProduct < 0) {
            dotProduct *= -1;
            hit.normal = Algeb.dotByScale(-1, hit.normal);
        }
        if (!firstHit) {
            switch (hit.model.getType()) {
                case LIGHT:

                    color = ((ObjLight) hit.model).getColor(origin, hit.point);
                    color = Algeb.dotByScale(Algeb.dot(hit.normal, Algeb.sub(origin, hit.point)), color);
                    break;
            }
        } else {

            switch (hit.model.getType()) {
                case LIGHT:

                    color = ((ObjLight) hit.model).getColor(origin, hit.point);
                    color = Algeb.dotByScale(Algeb.dot(hit.normal, Algeb.sub(origin, hit.point)), color);
                    break;

            }

        }

        return color;
    }

}
