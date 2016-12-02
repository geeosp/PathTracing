package geeosp.pathtracing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author geeo
 */
public class AlgebTest {

    public static void test() {
        //   ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
        double[] e1, e2, e3, e4, a, b, c, d, e;
        e1 = new double[]{1, 0, 0, 0};
        e2 = new double[]{0, 1, 0, 0};
        e3 = new double[]{0, 0, 1, 0};
        e4 = new double[]{0, 0, 0, 1};
        double[] e5 = new double[]{1.0, -2.0, -1.0, 3.0};
        a = Algb.soma(e1, e2);
        assert (Algb.isEquals(a, new double[]{1, 1, 0, 0})) : "Algeb.sum is wrong" + Algb.VectorToString(a);
        a = Algb.soma(e1, e5);
        assert (Algb.isEquals(a, new double[]{2, - 2, -1, 3})) : "Algeb.sum is wrong" + Algb.VectorToString(a);

        a = Algb.sub(e1, e2);
        assert (Algb.isEquals(a, new double[]{1, -1, 0, 0})) : "Algeb.sub is wrong" + Algb.VectorToString(a);
        double a1 = Algb.dot(e1, e2);
        assert (a1 == 0) : "Algeb.dot is wrong";
        a1 = Algb.dot(e1, e5);
        assert (a1 == 1);
        b = Algb.dotByScale(1.2, e5);
        assert (Algb.isEquals(b, new double[]{1.2, -2.4, -1.2, 1.2 * 3.0})) : "Algeb.dotByScale is wrong" + Algb.VectorToString(b);
        System.out.println(Algb.VectorToString(Algb.cross(e3, e1)));

        double[] incident = new double[]{1, 0, 0, 0};
        //incident = Algb.normalize(incident);
        System.out.println(Algb.VectorToString(incident));
        double[] reflected = Algb.reflect(incident, Algb.normalize(Algb.soma(e1, e2)));
        System.out.println(Algb.VectorToString(reflected));

    }

    
}
