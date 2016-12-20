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
    double[][] A;
    double[][] N;

    //Ax2 + By2 + Cz2 + Dxy  + Exz + Fyz + Gx + Hy + Iz + J =0
    public QuadricModel(String name, double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double[] objectMaterial) {
        super(name, new double[3], new double[3], new double[]{1, 1, 1}, Type.OBJECT);
        A = new double[][]{
                {a, d, e, g},
                {d, b, f, h},
                {e, f, c, i},
                {g, h, i, j}
        };
        N = new double[][]{
                {2 * a, d, e, g},
                {d, 2 * b, f, h},
                {e, f, 2 * c, i},
                {0, 0, 0, 0}
        };


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
    public Hit getNearestIntersectionPoint(double[] C, double[] D) {
        Hit hit = new Hit();
        D = Algb.normalize(D);
        double a, b, c;
        a = Algb.dot(D, Algb.matrixVectorProduct(A, D));
        b =
                Algb.dot(C, Algb.matrixVectorProduct(A, D))
                        +
                        Algb.dot(D, Algb.matrixVectorProduct(A, C))
        ;
        c = Algb.dot(C, Algb.matrixVectorProduct(A, C));
        double t = 0;
        if (Math.abs(a) < zeroDist) {
            t = -c / b;
            System.out.println(t);
        } else {
            double[] ts = Algb.solveQuadric(a, b, c);
            if (ts != null) {
                t = ts[0];
                if (t < zeroDist) {
                    t = ts[1];
                }


            }

        }
        if (t > zeroDist) {
            double[] p = Algb.soma(C, Algb.dotByScale(t, D));
            double[] n;

            hit.point = p;
            hit.model = this;
            hit.color = getColor();

            n = Algb.normalize(Algb.matrixVectorProduct(N, p));

            if (Algb.dot(n, D) > 0) {
                n = Algb.dotByScale(-1, n);
            }


            hit.normal = n;

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

    @Override
    public String toString() {

        return Algb.MatrixToString(A);
    }
}
