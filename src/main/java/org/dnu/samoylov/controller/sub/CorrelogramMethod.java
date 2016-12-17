package org.dnu.samoylov.controller.sub;

import org.dnu.samoylov.service.estimate.EstimateCalculator;
import org.dnu.samoylov.service.estimate.QuantilCalculator;

import java.util.ArrayList;
import java.util.List;

public class CorrelogramMethod {


    public static Result invoke(List<Double> dataSet) {
        int Ndiv2 = dataSet.size() / 2;
        int N = dataSet.size();
        List<Double> rKList = new ArrayList<>();

        for (int k = 0; k < Ndiv2; k++) {
            double rK;
            if (Ndiv2 < 25) {
                rK = calcRkLittle(dataSet, k);
            } else {
                rK = calcRkNormal(dataSet, k);
            }

            rKList.add(rK);
        }

        List<Double> tKList = getCharacteristic(N, rKList);

        boolean isExistCorrelation = false;
        for (int i = 1; i < rKList.size(); i++) {
            boolean isSignificant = Math.abs( tKList.get(i) ) > QuantilCalculator.StudentQuantil(N - i - 2);
            if (isSignificant) {
                isExistCorrelation = true;
                break;
            }
        }
        Result result = new Result();
        result.isExistCorrelation = isExistCorrelation;
        result.rKList = rKList;

        return result;
    }

    private static List<Double> getCharacteristic(int n, List<Double> rKList) {
        List<Double> tKList = new ArrayList<>(rKList.size());

        for (int k = 0; k < rKList.size(); k++) {
            Double rK = rKList.get(k);
            double t = rK * Math.sqrt( (n - k - 2) / (1 - rK * rK));
            tKList.add(t);
        }
        return tKList;
    }

    public static class Result {
        public List<Double> rKList = new ArrayList<>();
        public boolean isExistCorrelation = false;
    }


    public static double calcRkNormal(List<Double> dataSet, int k) {
        int N = dataSet.size();
        if (k > N / 2) {
            k = N / 2;
        }

        double uAverage = EstimateCalculator.calculateArithmeticMeanD(dataSet);
        double foo = 0;
        for (int i = 0; i < N - k; i++) {
            foo += (dataSet.get(i) - uAverage) * (dataSet.get(i + k) - uAverage);
        }

        double bar = 0;
        for (int i = 0; i < N; i++) {
            bar += (dataSet.get(i) - uAverage) * (dataSet.get(i) - uAverage) ;
        }

        return (1.0 / (N - k) * foo) / (bar / N);
    }

    public static double calcRkLittle(List<Double> dataSet, int k) {
        int N = dataSet.size();
        if (k > N / 2) {
            k = N / 2;
        }

        double uJSum = 0;
        double uJKSum = 0;
        for (int i = 0; i < N - k; i++) {
            uJSum += dataSet.get(i);
            uJKSum += dataSet.get(i + k);
        }


        double foo = 0;
        for (int i = 0; i < N - k; i++) {
            foo += (dataSet.get(i) - uJSum / (N - k)) * (dataSet.get(i + k) - uJKSum / (N - k));
        }
        foo /= (N - k);


        double bar1 = 0;
        for (int i = 0; i < N - k; i++) {
            double tmp = dataSet.get(i) - uJSum / (N - k);
            bar1 += tmp * tmp;
        }
        double bar2 = 0;
        for (int i = 0; i < N - k; i++) {
            double tmp = dataSet.get(i + k) - uJKSum / (N - k);
            bar2 += tmp * tmp;
        }


        double bar = Math.sqrt(bar1 * bar2 / (N - k) * (N - k));

        return foo / bar;
    }
}
