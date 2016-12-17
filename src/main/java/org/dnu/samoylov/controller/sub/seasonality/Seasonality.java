package org.dnu.samoylov.controller.sub.seasonality;

import org.dnu.samoylov.controller.sub.linear.Linear;
import org.dnu.samoylov.service.estimate.EstimateCalculator;

import java.util.ArrayList;
import java.util.List;

public class Seasonality {

    List<Double> dataSet, dataSetWithTrend;
    int period;
    public List<Double> seasonWave, forecast;
    public List<Double> a, b;

    public Seasonality(List<Double> dataSet, List<Double> dataSetWithTrend, int k, int period, int r, Linear linear) {

        this.dataSet = dataSet;
        this.dataSetWithTrend = dataSetWithTrend;

        this.period = period;

        initFourier(k);

        this.seasonWave  = initSeasonWave();
        this.forecast = initForecast(linear, r);
    }





    private List<Double> initForecast(Linear linear, int r) {
        forecast = new ArrayList<>(r);
        for (int i = 0; i < r; i++) {
            forecast.add(calcForecastLinear(linear, i));
        }

        return forecast;
    }

    private double calcForecastLinear(Linear linear, int r) {
        double trendLinear = linear.calcU(dataSet.size() + r);
        return trendLinear + seasonWave.get((dataSet.size() + r) % period);
    }

    //1 step
    private void initFourier(int k) {
        this.a = new ArrayList<>();
        this.b = new ArrayList<>();

        a.add(calcA0());
        for (int i = 1; i < k + 1; i++) {
            a.add(calcA(i));
            b.add(calcB(i));
        }
    }

    private double calcA0() {
        return EstimateCalculator.calculateArithmeticMeanD(dataSet);
    }

    private double calcA(int k) {
        double a = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            a += dataSet.get(i) * Math.cos((i + 1) * ((2.0 * Math.PI * k) / period));
        }
        a *= (2.0 / dataSet.size());
        return a;
    }

    private double calcB(int k) {
        double b = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            b += dataSet.get(i) * Math.sin((i + 1) * ((2.0 * Math.PI * k) / period));
        }
        b *= (2.0 / dataSet.size());
        return b;
    }


    //2 step
    private ArrayList<Double> initSeasonWave() {
        ArrayList<Double> seasonWave = new ArrayList<>(period);

        for (int i = 0; i < period; i++) {
            seasonWave.add(calcSCorrect(i));
        }

        return seasonWave;
    }

    private double calcSCorrect(int j) {
        double s = calcS(j);
        double sum = 0;
        for (int i = 0; i < period; i++) {
            sum += calcS(i);
        }
        sum *= (1.0 / period);
        s -= sum;
        return s;
    }


    private double calcS(int j) {
        double m, p;
        m = dataSet.size() / period;
        if (m * period + j < dataSet.size()) {
            p = m + 1;
        } else {
            p = m;
        }
        double s = 0;
        double aver = EstimateCalculator.calculateArithmeticMeanD(dataSet);
        for (int i = 0; i < p; i++) {
            s += dataSetWithTrend.get(j + i * period) - aver;
        }
        s *= (1.0 / p);
        return s;
    }

}
