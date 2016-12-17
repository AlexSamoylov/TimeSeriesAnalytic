package org.dnu.samoylov.controller.sub;

import org.dnu.samoylov.service.estimate.QuantilCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RecordMethod {

    public static List<Result> invoke(List<Double> dataSet) {
        final int n = dataSet.size();

        final int len = n - 1;



        ArrayList<Integer> mes = new ArrayList<>();
        mes.add(0);

        for (int i = 1; i < n; i++) {
            boolean isMax = true;
            for (int j = i - 1; j >= 0; j--) {
                if (dataSet.get(i) <= dataSet.get(j)) {
                    isMax = false;
                    mes.add(0);
                    break;
                }
            }
            if (isMax) {
                mes.add(1);
            }
        }



        ArrayList<Integer> les = new ArrayList<>();
        les.add(0);
        for (int i = 1; i < n; i++) {
            boolean isMin = true;
            for (int j = i - 1; j >= 0; j--) {
                if (dataSet.get(i) >= dataSet.get(j)) {
                    isMin = false;
                    les.add(0);
                    break;
                }
            }
            if (isMin) {
                les.add(1);
            }
        }

        int M = mes.stream().reduce((sum, m) -> sum+m).get();
        int L = les.stream().reduce((sum, l) -> sum+l).get();

        int D = M - L;
        int S = M + L;

        double mD = 0;

        double dD = 0;
        for (int i = 1; i < n; i++) {
            dD += 2d / i;
        }

        double mS = dD;
        double dS = 0;
        for (int i = 2; i < n; i++) {
            dS += 2d / i - 4d / (i*i);
        }

        double T1 = (D - mD) / Math.sqrt(dD);
        double T2 = (S - mS) / Math.sqrt(dS);


        double qantil =  QuantilCalculator.NormalQuantil();
        String result1;
        if (Math.abs(T1) < qantil) {
            result1 = "зміни середнього немає";
        } else if (T1 < -qantil) {
            result1 = "тенденція спадання середнього рівня";
        } else result1 = "тенденція зростання середнього рівня";


        String result2;
        if (Math.abs(T2) < qantil) {
            result2 = "зміни дисперсії немає";
        } else if (T2 < -qantil) {
            result2 = "тенденція спадання дисперсії";
        } else result2 = "тенденція зростання дисперсії";


        Result r1 = new Result((float) T1, (float) qantil, result1);
        Result r2 = new Result((float) T2, (float) qantil, result2);
        return Arrays.asList(r1, r2);
    }


}
