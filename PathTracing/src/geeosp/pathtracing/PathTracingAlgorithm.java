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
        i--;
        for (double y = 0; y < 2 * PI; y += dy) {
            for (double w = 0; w < PI; w += dw) {
                double a1 = 0;//-dw+rand.nextDouble()*dw;
                double a2 = 0;//-dy+ rand.nextDouble() *dy;
                brdf[i] = Algeb.normalize(Algeb.matrixVectorProduct(rotation(w + a1, y + a2), ref));//Algeb.normalize(ref);
                System.err.println(i);
                i--;

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
            for (int p = 0; p < scene.getNpaths(); p++) {
                if (hit.model.isLight()) {
                    color = Algeb.soma(color, hit.model.getColor(scene.getEye(), hit.point));
                } else {//is a object
                    DifuseModel model = (DifuseModel) hit.model;
                    //phong
                    double[] tmpAmb = new double[4];
                    double[] tmpSpec = new double[4];
                    double[] tmpDif = new double[4];
                    //ambiental
                    tmpAmb = Algeb.dotByScale(model.getKa(), scene.getAmbientColor());

                    double[] n = hit.normal;
                    //para cada fonte de luz
                    for (int s = 0; s < scene.getLights().size(); s++) {
                        //oege uma posicao aleatoria
                        Light light = (Light) scene.getLights().get(s);
                        double[] lightPosition = light.getOneLightPosition();
                        //calula a direcao da luz em relacao ao hit.point
                        double[] dir = Algeb.normalize(Algeb.sub(lightPosition, hit.point));
                        //cos do angulo entre a normal  e a direcao da luz
                        double cos = Algeb.dot(n, dir);
                        if (cos > 0) {
                            Hit inDirectionOfLight = getNextHit(hit.point, dir, scene);
                            //Se nao há nada  entre a luz e o ponto estudado
                            // if (hit.model == (scene.getLights().get(s))) {
                            tmpDif = Algeb.soma(tmpDif,
                                    new double[]{
                                        cos * light.getColor()[0] * model.getColor()[0],
                                        cos * light.getColor()[1] * model.getColor()[1],
                                        cos * light.getColor()[2] * model.getColor()[2],
                                        1.0
                                    });
                            //}
                            
                        }

                    }
                    color = Algeb.soma(color, tmpAmb);
                    color = Algeb.soma(color, tmpDif);

                }
            }
        } else {
            color = scene.getBackgroundColor();
        }
        color = toneMapping(color, scene);

        return color;
        //return hit.color;

    }

    double[] toneMapping(double[] color, RenderScene scene) {
        double tm = scene.getTonemapping();
        for (int i = 0; i < color.length; i++) {
            color[i] = color[i] / (color[i] + tm);
        }
        color[3] = 1.0;
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
        double[] color = scene.getBackgroundColor();
        if (deep == 0) {
            if (hit.isHit()) {
                color = hit.model.getColor(origin, hit.point);
            } else {
                color = scene.getAmbientColor();
                color[3] = color[0] == 0 ? 0 : 1;
            }

        } else if (hit.isHit()) {
            if (hit.model.getType() == Model.Type.LIGHT) {
                color = hit.model.getColor(origin, hit.point);
            } else {//n é uma luz
                double[] incident = Algeb.normalize(Algeb.sub(origin, hit.point));
                DifuseModel model = (DifuseModel) hit.model;
                if (Algeb.dot(incident, hit.normal) < 0) {
                    incident = Algeb.dotByScale(-1, incident);
                }
                double[] reflectedRay = reflect(incident, hit.normal);
                //  reflectedRay = new double[]{0,1,0,0};
                Hit next = getNextHit(hit.point, reflectedRay, scene);
                switch (rayType) {
                    case DIFUSE:

                        //    color = hit.color;
                        double[] reflectedColor = runAlgorithm(hit.point, next, rayType, scene, deep - 1);
                        double kd = model.getCoeficients()[1];
                        double[] temp = Algeb.dotByScale(kd, reflectedColor);
                        //color = Algeb.soma(Algeb.dotByScale(kd, color), temp);
                        color = Algeb.soma(hit.color, temp);
                        break;
                    case SPECULAR:
                        reflectedColor = runAlgorithm(hit.point, next, rayType, scene, deep - 1);
                        double ks = model.getCoeficients()[2];
                        temp = Algeb.dotByScale(ks, reflectedColor);
                        //color = Algeb.soma(Algeb.dotByScale(kd, color), temp);
                        color = Algeb.soma(hit.color, temp);
                        break;
                }
            }
        } else {//hit nothing
            color = scene.getBackgroundColor();
        }
        return color;
    }
}
