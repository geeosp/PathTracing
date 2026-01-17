/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing.renderers;

import geeosp.pathtracing.models.Hit;
import geeosp.pathtracing.models.Model;

import geeosp.pathtracing.models.ObjLight;
import geeosp.pathtracing.scene.RenderScene;

import java.util.Random;

import geeosp.pathtracing.Algb;
import geeosp.pathtracing.models.Material;

/**
 * @author geeo
 */
public class PathTracingRenderer extends RenderAlgorithm {

    final double PI = Math.PI;

    double[][] brdf;
    final Random rand = new Random();

    Model.Decoy decoy = Model.Decoy.NONE;


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
        double[] color = new double[4];
        double[] origin = scene.getEye();

        {
            //  System.out.print("oi");
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

            double antialiasing = .3;

            for (int r = 0; r < scene.getNpaths(); r++) {
                direction = Algb.sub(Algb.add(onScreen, new double[]{rand.nextGaussian() * deltax * antialiasing, deltay * rand.nextGaussian() * antialiasing, 0, 0}), origin);
                direction = Algb.normalize(direction);
                color = Algb.add(color, tracePath(origin, direction, scene, scene.getRayDepth(), scene.getBaseN()));
            }

        }
        return color;


    }

    double[] tracePath(double[] origin, double[] dir, RenderScene scene, int deep, double n) {
        double[] color = new double[]{0, 0, 0, 0};
        if (deep != 0) {
            Hit hit = getNextHit(origin, dir, scene);
            if (hit.isHit()) {
                double decoyFactor = 1;
                double d = Algb.distance(origin, hit.point);
                switch (decoy) {
                    case LINEAR:

                        decoyFactor = 1.0 / d;
                        break;
                    case QUADRATIC:
                        decoyFactor = 1.0 / (d * d);
                }
                //  System.out.println(hit.model.getName());
                if (hit.model.isLight()) {
                    color = Algb.dotByScale(2, hit.model.getColor());
                } else {
                    color = phongBase(hit, scene, origin);
                    Material m = hit.model.getMaterial();
                    double tot = 0
                            //    +m.ka
                            + m.ks
                            + m.kd
                            + m.kt;
                    double test = rand.nextDouble() * tot;
                    if (test < m.kd) {
                        double[] nextDir = Algb.randomVector();
                        double cosLN = Math.max(0, Algb.dot(nextDir, hit.normal));
                        if (cosLN < 0) {
                            nextDir = Algb.sub(new double[]{0, 0, 0, 0}, nextDir);
                            cosLN = -1 * cosLN;
                        }
                        //nextDir=hit.normal;//test

                        color = Algb.add(color,
                                Algb.dotByScale(cosLN * decoyFactor,
                                        Algb.crossdot(hit.color,
                                                tracePath(hit.point, nextDir, scene, deep - 1, n)
                                        )
                                )
                        );

                    } else if (test < m.kd + m.ks) {
                        double[] fromOrigin = Algb.normalize(Algb.sub(hit.point, origin));
                        double[] nextDir = Algb.reflect(fromOrigin, hit.normal);
                        //nextDir=hit.normal;
                        //from hitPoint

                        color = Algb.add(color,
                                Algb.dotByScale(decoyFactor,
                                        tracePath(hit.point, nextDir, scene, deep - 1, n)));
                    } else if (test < m.kd + m.ks + m.kt) {
                        double[] fromOrigin = Algb.normalize(Algb.sub(hit.point, origin));
                        double n1 = n;
                        double n2 = hit.model.getMaterial().n;
                        if (n1 == n2) {//o raio esta passeando dentro do objeto
                            n2 = scene.getBaseN();//ele vai sair
                        }
                        double[] nextDir = Algb.refract(fromOrigin, hit.normal, n, hit.model.getMaterial().n);
                        if (nextDir == null) {
                            nextDir = Algb.reflect(fromOrigin, hit.normal);
                            color = Algb.add(color,
                                    Algb.dotByScale(decoyFactor,
                                            tracePath(hit.point, nextDir, scene, deep - 1, n)));
                        } else {
                            color = Algb.add(color,
                                    Algb.dotByScale(decoyFactor,
                                            tracePath(hit.point, nextDir, scene, deep - 1, n2)
                                    )
                            );
                        }


                    }

                }

            } else {
              //  if (deep == scene.getRayDepth()){
                    color = scene.getBackgroundColor();
                //}
            }
        }
        return color;
    }

    double[] phongBase(Hit hit, RenderScene scene, double[] origin) {
        double[] color = new double[4];
        Model obj = hit.model;
        double[] ambient = Algb.dotByScale(scene.getAmbientColor() * obj.getMaterial().ka, obj.getColor());
        double[] diffuse = new double[4];
        double[] specular = new double[4];

        for (int l = 0; l < scene.getLights().size(); l++) {
            ObjLight lg = (ObjLight) scene.getLights().get(l);
            double[] lgPt = lg.getOnePoint();
            int liMax = 8;//number of trys per light
            double inveLiMax = 1.0 / liMax;
            int timesNotSeen = 0;
            for (int li = 0; li < liMax; li++) {
                if (canSee(lg, lgPt, hit.model, hit.point, scene)) {
                    double[] lgDir = Algb.normalize(Algb.sub(lgPt, hit.point));
                    double cosLN = Math.max(0, Algb.dot(lgDir, hit.normal));
                    double dist = Algb.getMagnitude(Algb.sub(lgPt, hit.point));
                    double f = inveLiMax;/// dist;///(dist*dist);
                    if (cosLN > 0) {
                        diffuse = Algb.add(diffuse,
                                Algb.dotByScale(cosLN * f * obj.getMaterial().kd,
                                        Algb.crossdot(lg.getColor(), hit.model.getColor())));
                    }
                    //from hitPoint
                    double[] fromOrigin = Algb.normalize(Algb.sub(hit.point, origin));
                    double[] reflected = Algb.reflect(lgDir, hit.normal);//TODO
                    double cosRO = Math.max(0, Algb.dot(fromOrigin, reflected));
                    if (cosRO > 0) {
                        specular = Algb.add(specular,
                                Algb.dotByScale(Math.pow(cosRO, hit.model.getMaterial().q) * f * obj.getMaterial().ks, lg.getColor()));
                    }
                } else {//lightCantSee
                    if (timesNotSeen < liMax) {
                        li--;
                        timesNotSeen++;
                    }
                }
            }
        }
        color = Algb.add(color, ambient);
        color = Algb.add(color, diffuse);
        color = Algb.add(color, specular);
        return color;
    }


    enum RayType {
        DIFUSE, SPECULAR, TRANSMITED
    }


}
