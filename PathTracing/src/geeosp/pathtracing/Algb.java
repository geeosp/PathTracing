package geeosp.pathtracing;

import java.util.Random;

public class Algb {

    // OPERACOES COM VETORES
    // SOMA
    public static double[] soma(double[] a, double[] b) {// soma dois vetores
        double[] soma = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            soma[i] = a[i] + b[i];
        }
        return soma;
    }

    public static double[] sub(double[] a, double[] b) {// subtrai 2 vetores

        double[] sub = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            sub[i] = a[i] - b[i];
        }
        return sub;
    }

    public static double dot(double[] a, double[] b) {// produto escalar
        // a.b
        double resp = 0;
        for (int i = 0; i < a.length; i++) {
            resp += a[i] * b[i];
        }
        return resp;
    }

    public static double[] dotByScale(double escalar, double[] vetor) {

        double[] k = new double[vetor.length];
        for (int i = 0; i < vetor.length; i++) {
            k[i] = escalar * vetor[i];
        }

        return k;
    }

    public static double[] crossdot(double[] v1, double[] v2) {
        double[] dot = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            dot[i] = v1[i] * v2[i];
        }
        return dot;

    }

    public static double getNorma(double[] v) {

        return Math.sqrt(Algb.dot(v, v));

    }

    public static double distance(double[] a, double[] b) {
        double d = getNorma(sub(a, b));
        if (d <= 0) {
            //   System.err.println(""+ d);
        }

        return d;

    }

    public static double[] cross(double[] a, double[] b) {
        double[] resp = new double[a.length];
        resp[0] = a[1] * b[2] - a[2] * b[1];
        resp[1] = a[2] * b[0] - a[0] * b[2];
        resp[2] = a[0] * b[1] - a[1] * b[0];
        return resp;
    }

    public static double[] normalize(double[] v) {

        double n = getNorma(v);

        return dotByScale((1.0 / n), v);
    }

    public static double[] projection(double[] u, double[] v) {
        double[] proj = new double[u.length];
        double a = 0, b = 0, k;
        double size = u.length;
        // a = <v*u> | b = <u*u>
        for (int i = 0; i < size; i++) {
            a = a + u[i] * v[i];
            b = b + v[i] * v[i];
            proj[i] = v[i];
        }

        k = a / b;

        // proj = v | k*proj = k*v
        for (int i = 0; i < size; i++) {
            proj[i] = k * proj[i];
        }

        return proj;
    }

    public static double[] matrixVectorProduct(double[][] M, double[] V) {
        int aux;
        double[] retorno = new double[V.length];
        for (int linha = 0; linha < M.length; linha++) {
            for (int coluna = 0; coluna < M[0].length; coluna++) {
                retorno[linha] += M[linha][coluna] * V[coluna];
            }

        }
        return retorno;
    }

    public static double[][] matrixMatrixProduct(double[][] a, double[][] b) {
        if (a[0].length != b.length) {
            throw new RuntimeException("Dimensões inconsistentes. Impossível multiplicar as matrizes");
        }
        double[][] ret = new double[a.length][b[0].length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                double[] bcolumn = new double[b.length];
                for (int k = 0; k < bcolumn.length; k++) {
                    bcolumn[k] = b[j][i];
                }
                ret[i][j] = dot(a[i], bcolumn);
            }
        }
        return ret;
    }
    public static double[] vectorMatrixProduct(double[] a, double[][] b) {
        if (a.length != b.length) {
            throw new RuntimeException("Dimensões inconsistentes. Impossível multiplicar as matrizes");
        }
        double[] ret = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                double[] bcolumn = new double[b.length];
                for (int k = 0; k < bcolumn.length; k++) {
                    bcolumn[k] = b[j][i];
                }
                ret[j] = dot(a, bcolumn);
            }
        }
        return ret;
    }

    public static String VectorToString(double[] v) {
        String retorno = "  ";
        for (int i = 0; i < v.length; i++) {
            retorno += " " + v[i];
        }
        return retorno;
    }

    public static String MatrixToString(double[][] v) {
        String ret = "";

        for (int i = 0; i < v.length; i++) {
            ret += "  ";
            for (int j = 0; j < v[i].length; j++) {
                ret += "" + v[i][j] + "  ";
            }
            ret += "\n";
        }

        return ret;

    }

    public static String MatrixToString(int[][] v) {
        String ret = "";

        for (int i = 0; i < v.length; i++) {
            ret += "  ";
            for (int j = 0; j < v[i].length; j++) {
                ret += "" + v[i][j] + "  ";
            }
            ret += "\n";
        }

        return ret;

    }

    // recebe a matriz de mudanca de base(MMB) e a matriz de pontos(MP)
    // faz: [v]a = [M]a ^b * [v]b Multiplica a MMB pela (MP)
    public static double[][] mudancaDeCoordenada(double[][] pontos,
                                                 double[][] MM, double[] C) {
        double[] V = new double[3];
        double[] R = new double[3];
        double[][] MatMud = new double[pontos.length][3];

        for (int i = 0; i < pontos.length; i++) {
            for (int j = 0; j < 3; j++) {
                V[j] = pontos[i][j];
            }
            R = matrixVectorProduct(MM, sub(V, C));
            MatMud[i] = R;
        }

        return MatMud;
    }

    public static String VectorToString(int[] v) {
        String retorno = "";
        for (int i = 0; i < v.length; i++) {
            retorno += " " + v[i];
        }
        return retorno;
    }

    public static double[] barCoef(double[] p, double[] v0, double[] v1, double[] v2) {
        double u, v;
        double[] v0v1, v0v2;
        v0v1 = sub(v1, v0);
        v0v2 = sub(v2, v0);
        double[] n = cross(v0v1, v0v2);
        double area2 = getNorma(n);
        double[] e1 = sub(v2, v1);
        double[] vp1 = sub(p, v1);
        double[] c = cross(e1, vp1);

        u = getNorma(c) / area2;
        if (dot(n, c) < 0) {
            u = -u;
        }
        double[] e2 = sub(v0, v2);
        double[] vp2 = sub(p, v2);
        c = cross(e2, vp2);
        v = getNorma(c) / area2;
        if (dot(n, c) < 0) {
            v = -v;
        }
        return new double[]{1 - u - v, u, v};
    }

    public static boolean isEquals(double[] v1, double[] v2) {

        for (int i = 0; i < v1.length; i++) {
            if (v1[i] != v2[i]) {
                return false;
            }
        }
        return true;
    }

    public static double[] randomVector() {
        double[] d = new double[4];
        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            d[i] = rand.nextGaussian();
        }
        return normalize(d);
    }

    public static double[] reflect(double[] incident, double[] normal) {//incident comes to 

        double x = 2.0 * Algb.dot(incident, normal);

        double[] reflect = Algb.sub(incident, Algb.dotByScale(x, normal));
        return reflect;

    }

    public static double[] refract(double[] incident, double[] normal, double n1, double n2) {
        double[] refract = null;
        double[] proj = projection(incident, normal);
        double[] k = sub(incident, proj);
        double s1 = getNorma(k);
        double s2 = s1 * n1 / n2;
        if (s2 <= 1) {
            double c2 = Math.sqrt(1 - s2 * s2);
            refract = new double[4];
            refract = soma(refract, dotByScale(s2 / s1, k));
            refract = soma(refract, dotByScale(c2, normalize(proj)));
        } else {
            //refract = reflect(incident, normal);
        }


        return refract;
    }

    public static double[] solveQuadric(double a, double b, double c) {
        double t1, t2;
        double d = b * b - 4 * a * c;
        if (d < 0)
            return null;
        else {
            t1 = (-b - Math.sqrt(d)) / (2 * a);
            if (d == 0) {
                t2 = t1;
            } else {
                t2 = (-b + Math.sqrt(d)) / (2 * a);
            }
        }


        return new double[]{Math.min(t1, t2), Math.max(t1,t2)};
    }

    public static double[][] transpose(double[][] A) {
        double[][] At = new double[A[0].length][A.length];
        for (int i = 0 ;i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                At[j][i] = A[i][j];
            }
        }

return At;
    }
}
