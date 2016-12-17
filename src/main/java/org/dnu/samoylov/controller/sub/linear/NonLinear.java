package org.dnu.samoylov.controller.sub.linear;

import java.util.List;
import java.util.stream.Collectors;

public class NonLinear extends Linear {

    public NonLinear(List<Double> dataSet) {
        super(reverse(dataSet));
    }

    private static List<Double> reverse(List<Double> dataSet) {
        return dataSet.stream()
                .map(x -> 1d / x)
                .collect(Collectors.toList());
    }

    public double calcU(int d) {
        return 1 / super.calcU(d);
    }
}