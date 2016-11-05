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
                brdf[i] = Algeb.normalize(Algeb.multMatrizVetor(rotation(w + a1, y + a2), ref));//Algeb.normalize(ref);
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
        return Algeb.multMatrixMatrix(r1, r2);
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

        double[] onScreen = new double[]{
            x0 + (i + 1) * deltax,
            y0 + (j + 1) * deltay,
            0,
            1
        };
        direction = Algeb.sub(onScreen, scene.getEye());
        direction = Algeb.normalize(direction);

        hit = getNextHit(scene.getEye(), direction, scene);

        double[] color = new double[]{0, 0, 0, 1};

        if (hit.isHit()) {

            switch (hit.model.getType()) {
                case LIGHT:
                    color = ((ObjLight) hit.model).getColor(scene.getEye(), hit.hitPoint);
                    break;
                case OBJECT:
                    Random rand = new Random();
                    DifuseModel model = (DifuseModel) hit.model;
                    double ka = model.getCoeficients()[0];
                    double kd = model.getCoeficients()[1];
                    double ks = model.getCoeficients()[2];
                    double kt = model.getCoeficients()[3];
                    double ktot = kd + ks + kt;
                    //  color = new double[]{ka * hit.color[0], ka * hit.color[1], ka * hit.color[2], hit.color[3]};

                    for (int r = 0; r < brdf.length; r++) {
                        double random = rand.nextDouble() * ktot;
                        double[] ray = brdf[r];

                        if (Algeb.dot(ray, hit.hitNormal) < 0) {
                            ray = Algeb.prodByEscalar(-1, ray);
                        }

                        Hit other = getNextHit(hit.hitPoint, ray, scene);
                        if (other.isHit()) {
                            switch (other.model.getType()) {
                                case LIGHT:
                                    color = Algeb.soma(color, ((ObjLight) other.model).getColor(other.hitPoint, hit.hitPoint));
                                    break;
                                case OBJECT:

                                    break;
                            }

                        }

                        //    color = ((ObjDifuseModel) hit.model).getColor(scene.getEye(), hit.hitPoint);
                        break;
                    }

            }
        }
        return color;
        // return hit.color;

    }

    double[] reflect(double[] incident, double[] normal
    ) {
        double[] x = Algeb.projec(incident, normal);
        double[] reflect = Algeb.soma(incident, Algeb.prodByEscalar(-2, x));
        return reflect;

    }

    Hit getNextHit(double[] origin, double[] direction, RenderScene scene
    ) {
        Hit hit = new Hit();
        for (int t = 0; t < scene.getModels().size(); t++) {
            Model a = scene.getModels().get(t);
            Hit temp = a.getNearestIntersectionPoint(scene.getEye(), direction);
            if (temp.isHit()) {
                double tempDist = Algeb.distancia(temp.hitPoint, scene.getEye());
                if (tempDist < Algeb.distancia(hit.hitPoint, scene.getEye())) {
                    hit = temp;
                }
            }
        }
        return hit;
    }

}
