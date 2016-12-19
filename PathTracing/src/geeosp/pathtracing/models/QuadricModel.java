package geeosp.pathtracing.models;

import com.sun.javafx.sg.prism.NGShape;
import geeosp.pathtracing.Algb;

/**
 * Created by Geovane on 19/12/2016.
 */
public class QuadricModel extends Model {
    private double[] coeficients;
    private Material material;
    //F(x, y, z) = Ax2 + By2 + Cz2 + Dxy+ Exz + Fyz + Gx + Hy + Iz + J = 0
    double A, B, C, D, E, F, G, H, I, J;


    public QuadricModel(String name, double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double[] objectMaterial) {
        super(name, new double[3], new double[3], new double[]{1, 1, 1}, Type.OBJECT);
        A = a;
        B = b;
        C = c;
        D = d;
        E = e;
        F = f;
        G = g;
        H = h;
        I = i;
        J = j;
        this.material = new Material(new double[]{
                objectMaterial[0],
                objectMaterial[1],
                objectMaterial[2],
                1.0},
                objectMaterial[3],
                objectMaterial[4],
                objectMaterial[5],
                objectMaterial[6],
                objectMaterial[7],
                objectMaterial[8]
        );
    }

    @Override
    public Hit getNearestIntersectionPoint(double[] o, double[] d) {
        Hit hit = new Hit();
        double Aq, Bq, Cq;
        Aq = A * d[0] * d[0]
                + B * d[1] * d[1]
                + C * d[2] * d[2]
                + D * d[0] * d[1]
                + E * d[0] * d[2]
                + F * d[1] * d[2]
        ;
        Bq = 2 * A * o[0] * d[0]
                + 2 * B * o[1] * d[1]
                + 2 * C * o[2] * o[2]
                + D * (o[0] * d[1] + d[0] * o[1])
                + E * (o[0] * d[2] + d[0] * o[2])
                + F * (o[1] * d[2] + d[1] * o[2])
                + G * d[0]
                + H * d[1]
                + I * d[2]
        ;
        Cq = A * o[0] * o[0]
                + B * o[1] * o[1]
                + C * o[2] * o[2]
                + D * o[0] * o[1]
                + E * o[0] * o[2]
                + F * o[1] * o[2]
                + G * o[0]
                + H * o[1]
                + I * o[2]
                + J
        ;
        double t = 0;
        if (Aq == 0) {
            t = -Cq / Bq;
        } else {
            if (Bq * Bq - 4 * Aq * Cq > 0) {
                t = (-Bq - Math.sqrt(Bq*Bq-4*Aq*Cq))/(2*Aq);
                if(t<zeroDist){
                    t = (-Bq + Math.sqrt(Bq*Bq-4*Aq*Cq))/(2*Aq);
                }
            }
        }

        if(t>=zeroDist){
            double []p=Algb.soma(o, Algb.dotByScale(t, d));
            double []n= Algb.normalize(
              new double[]{
                     2*A*p[0]+D*p[1]+C*p[2]+G,
                      2*B*p[0]+D*p[1]+F*p[2]+H,
                      2*C*p[0]+E*p[1]+F*p[2]+I,
                      1
              }


            );

            hit.point=p;
            hit.model=this;
            hit.color=getColor();
            if (Algb.dot(n,d)>0){
                n=Algb.dotByScale(-1,n);
            }
            hit.normal=n;





        }



        return hit;
    }

    @Override
    public double[] getColor() {
        return this.material.color;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }


    @Override
    public double[] getColor(double[] origin, double[] point, Decoy decoy) {
        double[] ret = getColor();

        double dist = Algb.distance(origin, point);
        double scale = 1;
        switch (decoy) {
            case LINEAR:
                scale *= 1.0 / dist;
                break;
            case QUADRATIC:
                scale *= 1.0 / (dist * dist);
        }

        ret = Algb.dotByScale(scale, ret);
        ret[3] = 1;
        return ret;
    }
}
