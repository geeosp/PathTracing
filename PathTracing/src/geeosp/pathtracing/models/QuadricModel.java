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
    double minx, maxX, minY, maxY, minZ, maxZ;

    //Ax2 + By2 + Cz2 + Dxy  + Exz + Fyz + Gx + Hy + Iz + J =0
    public QuadricModel(String name, double a, double b, double c, double d, double e, double f, double g, double h, double i, double j, double minX, double maxX, double minY, double maxY, double minZ, double maxZ, double[] objectMaterial) {
        super(name, new double[3], new double[3], new double[]{1, 1, 1}, Type.OBJECT);
        d = d / 2;
        e = e / 2;
        f = f / 2;
        g = g / 2;
        h = h / 2;
        i = i / 2;
        this.minx = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        A = new double[][]{
                {a, d, e, g},
                {d, b, f, h},
                {e, f, c, i},
                {g, h, i, j}
        };
        N = new double[][]{
                {2 * a, 2 * d, 2 * e, g},
                {d, 2 * b, 2 * f, 2 * h},
                {2 * e, 2 * f, 2 * c, 2 * i},
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
        // D = Algb.normalize(D);
        double a, b, c;

        a = Algb.dot(D, Algb.matrixVectorProduct(A, D));
        b =
                Algb.dot(C, Algb.matrixVectorProduct(A, D))
                        +
                        Algb.dot(D, Algb.matrixVectorProduct(A, C))
        ;
        c = Algb.dot(C, Algb.matrixVectorProduct(A, C));
        double[] p;
        double t = 0;
        if (Math.abs(a) < zeroDist) {
            t = -c / b;
            // System.out.println(t);
        } else {
            double[] ts = Algb.solveQuadric(a, b, c);
            if (ts != null) {
                t = ts[0];
                p = Algb.soma(C, Algb.dotByScale(t, D));
                if (t < zeroDist || !isInBoundBox(p)) {
                    t = ts[1];
                }
            }
        }
        if (t > zeroDist) {
            p = Algb.soma(C, Algb.dotByScale(t, D));
            if (isInBoundBox(p)) {
                double[] n;
                hit = new Hit();
                //  System.out.println (Algb.VectorToString(p));
                hit.point = p;
                hit.model = this;
                hit.color = getColor();

                n = Algb.normalize(Algb.matrixVectorProduct(N, p));

                if (Algb.dot(n, D) > 0) {
                    n = Algb.dotByScale(-1, n);
                }


                hit.normal = n;
            }
        }


        return hit;
    }

    boolean isInBoundBox(double[] p) {
        double x = p[0];
        double y = p[1];
        double z = p[2];
        if((x >= minx && x <= maxX) && (y >= minY && y <= maxY) && (z >= minZ && z <= maxZ)) {
         //  System.out.println(Algb.VectorToString(p));
            return true;
        }else{
            return  false;
        }
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
