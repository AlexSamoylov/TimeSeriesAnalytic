package org.dnu.samoylov.service.estimate;

public class QuantilCalculator {

    private static double Normal_C0 = 2.515517;
    private static double Normal_C1 = 0.802853;
    private static double Normal_C2 = 0.010328;
    private static double Normal_D0 = 1.432788;
    private static double Normal_D1 = 0.1892659;
    private static double Normal_D2 = 0.001308;


    public static final float DEF_ALPHA = 0.05f;

    public static double NormalQuantil() {
        return NormalQuantil(1 - DEF_ALPHA / 2);
    }
    public static double NormalQuantil(double p) {
        double result = 0;
        double a, t;

        if (p > 0.5)
            a = 1.0 - p;
        else
            a = p;

        t = Math.sqrt(-2.0 * Math.log(a));
      //  t = Math.round(t);

        result = t - ((Normal_C0 + (Normal_C1 * t) + (Normal_C2 * t * t)) / (1 + (Normal_D0 * t) + (Normal_D1 * t * t) + (Normal_D2 * t * t * t)));
        //result = Math.round(result);

        if (!(p > 0.5))
            result = result * (-1.0);

        return (result);
    }

    public static double StudentQuantil(double v) {
        return StudentQuantil(1 - DEF_ALPHA / 2, v);
    }
    public static double StudentQuantil(double p, double v) {
        double result;
        double up;

        up = NormalQuantil(p);

        result = up + ((1.0 / v) * StudentG1(up)) + ((1.0 / Math.pow(v, 2)) * StudentG2(up)) + ((1.0 / Math.pow(v, 3)) * StudentG3(up)) + ((1.0 / Math.pow(v, 4)) * StudentG4(up));
        //result = Math.round(result);

        return (result);
    }


    private static double StudentG1(double up) {
        double result;

        result = (1.0 / 4.0) * (Math.pow(up, 3) + up);
        //result = Math.round(result);

        return (result);
    }

    private static double StudentG2(double up) {
        double result;

        result = (1.0 / 96.0) * ((5.0 * Math.pow(up, 5)) + (16.0 * Math.pow(up, 3))  + (3.0 * up));
      //  result = Math.round(result);

        return (result);
    }

    private static double StudentG3(double up) {
        double result;

        result = (1.0 / 384.0) * ((3.0 * Math.pow(up, 7)) + (19.0 * Math.pow(up, 5)) + (17.0 * Math.pow(up, 3)) - (15.0 * up));
        //result = Math.round(result);

        return (result);
    }

    private static double StudentG4(double up) {
        double result;

        result = (1.0 / 92160.0) * ((79.0 * Math.pow(up, 9)) + (779.0 * Math.pow(up, 7)) + (1482.0 * Math.pow(up, 5)) - (1920.0 * Math.pow(up, 3)) - (945.0 * up));
       // result = Math.round(result);

        return (result);
    }


    public static double FisherQuantil(double v1, double v2) {
        return FisherQuantil(1 - DEF_ALPHA, v1, v2);
    }
    public static double FisherQuantil(double p, double v1, double v2) {
        return Math.exp(2*fisherZ(p, v1, v2));
    }

    public static double fisherZ(double p, double v1, double v2) {
        double up = -1*NormalQuantil(p);
        double res = up * Math.sqrt(fisherSigma(v1, v2) / 2);
        res -= 1.0/6 * fisherGamma(v1, v2) * (up * up + 2);
        res += Math.sqrt(fisherSigma(v1, v2) / 2) * (fisherSigma(v1, v2)/24 *
                (up * up + 3 * up) + (1.0/72) * (Math.pow(fisherGamma(v1, v2),2) / fisherSigma(v1, v2)) *
                (Math.pow(up, 3) + 11*up));
        res -= ((fisherGamma(v1, v2) * fisherSigma(v1, v2)) / 120) * (Math.pow(up, 4) + 9 * Math.pow(up, 2) + 8);
        res += Math.pow(fisherGamma(v1, v2), 3) / (3240 * fisherSigma(v1, v2)) * (3 * Math.pow(up, 4) +
                7 * Math.pow(up, 2) -16);
        res += Math.sqrt(fisherSigma(v1, v2) / 2) * ( Math.pow(fisherSigma(v1, v2), 2)/1920 *
                (Math.pow(up, 5) + 20 * Math.pow(up, 3) + 15 * up) +
         + ( Math.pow(fisherGamma(v1, v2), 4) / 2880 ) * (Math.pow(up, 5) + 44 * Math.pow(up, 3) + 183 * up)
         + ( Math.pow(fisherGamma(v1, v2), 4) / ( 155520 * fisherSigma(v1, v2) ) ) *
                (9 * Math.pow(up, 5) - 284 * Math.pow(up, 3) - 1513 * up));
        return res;
    }

    public static double fisherSigma(double v1, double v2) {
        return 1.0/v1 + 1.0/v2;
    }

    public static double fisherGamma(double v1, double v2) {
        return 1.0 / v1 - 1.0/v2;
    }



















}
