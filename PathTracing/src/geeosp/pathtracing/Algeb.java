package geeosp.pathtracing;

public class Algeb {

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

    public static double distance(double[] a, double[] b) {
        double d = 0;
        for (int i = 0; i < a.length; i++) {
            d += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return (Math.sqrt(d));

    }

    public static double distanceSqr(double[] a, double[] b) {
        double d = 0;
        for (int i = 0; i < a.length; i++) {
            d += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return d;

    }

    public static double dot(double[] a, double[] b) {// produto escalar
        // a.b
        double resp = 0;
        for (int i = 0; i < a.length; i++) {
            resp += a[i] * b[i];
        }
        return resp;
    }

    public static double[] crossdot(double[] a, double[] b) {// produto escalar
        // a.b
        double[] resp = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            resp[i] = a[i] * b[i];
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

    public static double[] cross(double[] a, double[] b) {
        double[] resp = new double[a.length];
        resp[0] = a[1] * b[2] - a[2] * b[1];
        resp[1] = a[2] * b[0] - a[0] * b[2];
        resp[2] = a[0] * b[1] - a[1] * b[0];
        return resp;
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

    public static double getNorma(double[] v) {
        double k = 0;
        for (int i = 0; i < v.length; i++) {
            k = k + v[i] * v[i];
        }
        k = Math.sqrt(k);
        return k;
    }

    public static double[] normalize(double[] v) {
        double n = 0;
        n = getNorma(v);
        double[] v2 = v;
        for (int i = 0; i < v.length; i++) {
            v2[i] = v2[i] / n;
        }
        return v2;
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



    public static void test(){
        double[] e1 = {1, 0, 0, 0};
        double [] e2  ={ 0, 1, 0, 0};
        double [] e3 = { 0,0,1,0};
        double [] e4 = { 0, 0 , 0, 1};
    double [] p1 = {1, 2, 3};
    double [] p2  = { 3, 4, 5} ;

    assert( e1 == new double[] {1,1,0,0});






    }
}
