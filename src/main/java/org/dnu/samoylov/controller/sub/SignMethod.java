package org.dnu.samoylov.controller.sub;

import org.dnu.samoylov.service.estimate.QuantilCalculator;

import java.util.ArrayList;
import java.util.List;

public class SignMethod {

    public static Result invoke(List<Double> dataSet) {

        final int n = dataSet.size();
        final int len = n - 1;
        final List<Integer> xes = new ArrayList<>();

        for (int i = 0; i < len; i++) {
            int xi = dataSet.get(i) < dataSet.get(i + 1) ? 1 : 0;
            xes.add(xi);
        }

        int c = xes.stream().reduce((x1, x2) -> x1+x2).get();
        double mC = (n-1)/2d;
        double dC = (n + 1)/12d;
        double k = (c - mC) / Math.sqrt(dC);


        double qantil =  QuantilCalculator.NormalQuantil();
        String result;
        if (Math.abs(k) < qantil) {
            result = "ряд випадковий";
        } else if (k < -qantil) {
            result = "тенденція спадання";
        } else result = "тенденція зростання";

        return new Result((float) k, (float) qantil, result);
    }


}
