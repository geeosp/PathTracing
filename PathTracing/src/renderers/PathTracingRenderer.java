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
import geeosp.pathtracing.models.Material;

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
                    Random rand = new Random();
                    //phong
                    double[] phong = phongBase(hit, scene, scene.getEye());
                    fator = Algb.soma(fator, phong);

                    Material m = hit.model.getMaterial();
                    double ktot = m.kd + m.ks;
                    double test = rand.nextDouble() * ktot;
                    int deep = 0;
                    if (test < m.kd) {
                        double[] dir = brdf[p];
                        if(Algb.dot(dir, hit.normal)<0){
                            dir = Algb.dotByScale(-1, dir);
                        }
                        fator = Algb.soma(fator, Algb.dotByScale(m.kd, tracePath(hit.point, dir, RayType.DIFUSE, scene, deep)));
                    }

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
double[] tracePath(double[] origin, double[] dir, RayType rayType, RenderScene scene, int deep) {
        double[] color = new double[4];
        Hit hit = getNextHit(origin, dir, scene);
        if (hit.isHit()) {
            if(hit.model.isLight()){
                color =  hit.color;
            }else{
                color = phongBase(hit, scene, origin);
                
            
            
            }
            
        }

        //color = new double[] {1.0, 0, 1.0, 1.0};
        return color;
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
            int liMax = 1;
            double inveLiMax = 1.0 / liMax;
            for (int li = 0; li < liMax; li++) {
                if (canSee(lg, lgPt, hit.model, hit.point, scene)) {
                    double[] lgDir = Algb.normalize(Algb.sub(lgPt, hit.point));
                    double cos = cos = Math.max(0, Algb.dot(lgDir, hit.normal));
                    double dist = Algb.getNorma(Algb.sub(lgPt, hit.point));
                    double f = inveLiMax;/// dist;///(dist*dist);
                    diffuse = Algb.soma(diffuse, Algb.dotByScale(cos * f * obj.getMaterial().kd, Algb.crossdot(lg.getColor(), hit.model.getColor())));
                    //speccular
                    //     double[] reflected = reflect(lgDir, hit.normal);
                    //   double[] toEye = Algb.normalize(Algb.sub(eye, hit.point));
                    //  cos = Math.max(0, Algb.dot(toEye, reflected));
                    // specular = Algb.soma(specular, Algb.dotByScale(Math.pow(cos, hit.model.getMaterial().n) * f * obj.getMaterial().ks, lg.getColor()));
                }
            }
        }
        color = Algb.soma(color, ambient);
        color = Algb.soma(color, diffuse);
        //   color = Algb.soma(color, specular);
        return color;
    }

    double[] reflect(double[] incident, double[] normal
    ) {

        double x = 2.0 * Algb.dot(incident, normal);

        double[] reflect = Algb.sub(Algb.dotByScale(x, normal), incident);
        return reflect;

    }

    enum RayType {
        DIFUSE, SPECULAR, TRANSMITED
    };

    
}
