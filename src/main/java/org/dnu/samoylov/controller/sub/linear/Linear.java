package org.dnu.samoylov.controller.sub.linear;

import org.dnu.samoylov.service.estimate.EstimateCalculator;
import org.dnu.samoylov.util.onetimerun.OneTimeRunMethod;
import org.dnu.samoylov.util.onetimerun.RunLambda;

import java.util.List;

public class Linear {
    List<Double> dataSet;
    private Double arithmeticMean;

    public Linear(List<Double> dataSet) {
        this.dataSet = dataSet;
        arithmeticMean = EstimateCalculator.calculateArithmeticMeanD(dataSet);
    }


    public double calcU(int d) {
        return getA() + getB() * (d + 1); //d start with 0, but need with 1
    }

    private final OneTimeRunMethod<Double> aGetter = ((RunLambda<Double>) () ->
            arithmeticMean - getB() * calcAvaregeOfTime())
            .method();

    private final OneTimeRunMethod<Double> bGetter = ((RunLambda<Double>) () ->
            (calcAvaregeOfSelectionAndTime() - arithmeticMean * calcAvaregeOfTime()) /
                    (calcAvaregeOfPowTime() - Math.pow(calcAvaregeOfTime(), 2)))
            .method();

    public double getA() {
        return aGetter.get();
    }

    public double getB() {
        return bGetter.get();
    }

    public double getFTest() {
        double SSR = 0;

        for (int i = 0; i < dataSet.size(); i++) {
            SSR += Math.pow(calcU(i) - arithmeticMean, 2);
        }
        double MSE = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            MSE += Math.pow(dataSet.get(i) - calcU(i), 2);
        }
        MSE /= (dataSet.size() - 2);

        return SSR / MSE;
    }

    public double getR2() {

        double SSR = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            SSR += Math.pow(calcU(i) - arithmeticMean, 2);
        }

        double SST = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            SST += Math.pow(dataSet.get(i) - arithmeticMean, 2);
        }

        if (SST == 0) {
            return 1;
        }


        return SSR / SST;
    }


    protected double calcAvaregeOfTime() {
        return ((double) (1 + dataSet.size()) / 2);
    }

    protected double calcAvaregeOfSelectionAndTime() {
        double sum = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            sum += dataSet.get(i) * (i + 1);
        }
        sum /= dataSet.size();
        return sum;
    }

    protected double calcAvaregeOfPowTime() {
        double sum = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            sum += Math.pow(i + 1, 2);
        }
        sum /= dataSet.size();
        return sum;
    }

}
