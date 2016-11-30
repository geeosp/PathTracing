/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package renderers;

import geeosp.pathtracing.Algb;
import geeosp.pathtracing.models.Hit;
import geeosp.pathtracing.models.Model;

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
public class DistanceRenderer extends RenderAlgorithm {

    final double threshold = .01;
    final double PI = Math.PI;
    double[][] brdf;

    public DistanceRenderer() {
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
        for (double y = 0; y < 2 * PI; y += dy) {
            for (double w = 0; w < PI; w += dw) {
                i--;
                if (i >= 0) {
                    double a1 = 0;//-dw+rand.nextDouble()*dw;
                    double a2 = 0;//-dy+ rand.nextDouble() *dy;
                    brdf[i] = Algb.normalize(Algb.matrixVectorProduct(rotation(w + a1, y + a2), ref));//Algeb.normalize(ref);
                }

            }
        }
        // System.out.println(Algb.MatrixToString(brdf));

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

                    break;
                case OBJECT:

                 Model difuseModel =hit.model;
                    for (int l = 0; l < scene.getLights().size(); l++) {
                        ObjLight lg = (ObjLight) scene.getLights().get(l);

                      //if (canSee(lg.getOnePoint(), lg, hit.point, hit.model, scene)) {
                            // if(Algeb.distance(h.point, hit.point)<=threshold){
                            double d = Algb.distance(hit.point, scene.getEye());
                            color = Algb.dotByScale(500.0/(d*d), new double[]{1, 1, 1.0, 1.0});
color[3]=1;
                            //}
                    // }
                    }
                    /*for (int r = 0; r < brdf.length; r++) {
                        Random rand = new Random();
                        double ka = ((DifuseModel) hit.model).getCoeficients()[0];
                        double ks = ((DifuseModel) hit.model).getCoeficients()[2];
                        double kd = ((DifuseModel) hit.model).getCoeficients()[1];
                        double ktot = ka + kd + ks;
                        double[] ray = brdf[r];
                        if (Algeb.dot(ray, hit.normal) < 0) {
                            ray = Algeb.dotByScale(-1, ray);
                        }
                        Hit test = getNextHit(hit.point, ray, scene);
                        double random = rand.nextDouble() * (ktot);
                        double[] fator = new double[4];
                        if (random < ka) {
                            fator = Algeb.dotByScale(ka * scene.getAmbientColor() / brdf.length, hit.color);
                        } else if (random < ka + kd) {
                           // fator = Algeb.dotByScale(kd / brdf.length, tracePath(hit.point, test, RayType.DIFUSE, scene, 5));
                        } else if (random < ka + kd + ks) {
                              fator = Algeb.dotByScale(kd/ brdf.length, tracePath(hit.point, test, RayType.SPECULAR, scene, 5));

                        }
                        color = Algeb.soma(color, fator);

                    }*/

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

    boolean canSee(double[] originPoint, Model originModel, double[] targetPoint, Model targetModel, RenderScene scene) {
        Hit hit = null;
        double[] direction = Algb.sub(targetPoint, originPoint);
        direction = Algb.normalize(direction);
        for (int t = 0; t < scene.getModels().size(); t++) {
            Model a = scene.getModels().get(t);
            if (!a.getName().equals(originModel.getName())) {
                Hit temp = a.getNearestIntersectionPoint(originPoint, direction);
                if (temp.isHit()) {
                    if (hit == null) {
                        hit = temp;
                    }
                    double tempDist = Algb.distance(temp.point, originPoint);
                    
                    if (tempDist < Algb.distance(hit.point, originPoint)) {
                        hit = temp;
                    }
                }
            }
        }

        return (hit.model != null) && hit.model.getName().equals(targetModel.getName());
    }
}
