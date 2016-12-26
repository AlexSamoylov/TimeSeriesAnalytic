package org.dnu.samoylov.controller.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dnu.samoylov.controller.sub.linear.Linear;
import org.dnu.samoylov.controller.sub.seasonality.Seasonality;
import org.dnu.samoylov.service.estimate.QuantilCalculator;
import org.dnu.samoylov.util.SpringFXMLLoader;
import org.dnu.samoylov.util.ZoomedLineChart;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
public class SeasonalityController extends MainMainController implements Initializable {

    public TextField periodTF;
    public TextField kTF;
    public TextField rTF;

    public ListView<String> seasonWaveLv;
    public ListView<String> forecastLv;
    public ListView<String> fourerLv;

    public ListView<String> compareView;
    public ListView balanceLv;
    public ZoomedLineChart balanceChart;


    private Linear linear;
    private List<Double> dataSet;
    private List<Double> withoutTrendDataSet;

    public static void showSeasonality(ActionEvent event, Linear linear, List<Double> dataSet, List<Double> withoutTrendDataSet) {

        final Group root = new Group();
        final Stage stage = new Stage();

        final SeasonalityController controller = (SeasonalityController) SpringFXMLLoader.load("/fxml/seasonality.fxml");
        root.getChildren().add(controller.getView());
        final Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());

        stage.show();

        if (dataSet != null) {
            controller.init(linear, dataSet, withoutTrendDataSet);
        }
    }

    private void init(Linear linear, List<Double> dataSet, List<Double> withoutTrendDataSet) {
        this.linear = linear;
        this.dataSet = dataSet;
        this.withoutTrendDataSet = withoutTrendDataSet;
    }

    List<Double> selectionSeasonalityWithTrendLinear;

    @FXML
    public void loadData() {

        Seasonality seasonalityLinear = new Seasonality(withoutTrendDataSet,
                dataSet,
                Integer.valueOf(kTF.getText()),
                Integer.valueOf(periodTF.getText()),
                Integer.valueOf(rTF.getText()),
                linear);


        InitFourerDGV(seasonalityLinear);
        InitSeasonWaveDGV(seasonalityLinear);
        InitForecastDGV(seasonalityLinear);

        buildGist(dataSet);

        selectionSeasonalityWithTrendLinear = new ArrayList<>(dataSet.size());

        Integer period = Integer.valueOf(periodTF.getText());
        for (int i = 0; i < dataSet.size(); i++) {
            Double tmp_data = linear.calcU(i) + seasonalityLinear.seasonWave.get( i % period);
            selectionSeasonalityWithTrendLinear.add(tmp_data);
        }

        Integer r = Integer.valueOf(kTF.getText());

        List<Double> furFur = new ArrayList<>(dataSet.size());
        for (int i = 0; i < dataSet.size(); i++) {
            double sum = 0;
            for (int j = 0; j < r; j++) {
                sum += seasonalityLinear.a.get(j + 1) * Math.cos((i + 1) * ((2.0 * Math.PI * (j + 1)) / period))
                        + seasonalityLinear.b.get(j) * Math.sin((i + 1) * ((2.0 * Math.PI * (j + 1)) / period));
            }

            double point = linear.calcU(i) + (seasonalityLinear.a.get(0) + sum);
            furFur.add(point);
        }




        addNewLine(selectionSeasonalityWithTrendLinear, "индекс сезонності");
        addNewLine(furFur, "Фурье");
        LinkedList<Double> forecastWithLast = new LinkedList<>();
        forecastWithLast.add(selectionSeasonalityWithTrendLinear.get(selectionSeasonalityWithTrendLinear.size() -1 ) );
        forecastWithLast.addAll(seasonalityLinear.forecast);

        addNewLine(forecastWithLast, "прогнозування", dataSet.size() - 1);

        InitDGVSelectionAndSeasonality(dataSet, selectionSeasonalityWithTrendLinear, seasonalityLinear.forecast);



        List<Double> selectionBalancesLinear = new ArrayList<>(dataSet.size());
        for (int i = 0; i < dataSet.size(); i++) {
            Double tmp_data = dataSet.get(i) - selectionSeasonalityWithTrendLinear.get(i);
            selectionBalancesLinear.add(tmp_data);
        }

        InitDGVBalances(selectionBalancesLinear);
        buildTable(selectionBalancesLinear);

    }

    private void InitDGVBalances(List<Double> selectionBalancesLinear) {
        ObservableList items = balanceLv.getItems();
        items.clear();

        items.addAll(selectionBalancesLinear);

        balanceChart.getData().clear();
        balanceChart.layout();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (int i = 0; i < dataSet.size(); i++) {
            series.getData().add( new XYChart.Data<>(i, selectionBalancesLinear.get(i)) );
        }


        balanceChart.getData().add(series);
        balanceChart.resetZoom();
    }


    private void InitDGVSelectionAndSeasonality(List<Double> dataSet, List<Double> selectionSeasonalityWithTrend, List<Double> forecast) {
        ObservableList<String> items = compareView.getItems();
        items.clear();


        for (int i = 0; i < dataSet.size(); i++) {
            String s = (i+1) + ": " + dataSet.get(i) + "\t" + selectionSeasonalityWithTrend.get(i);
            items.add(s);
        }

        for (int i = 0; i < forecast.size(); i++) {
            String s = (dataSet.size() + i +1) + ": " + "\t\t" + "\t" + forecast.get(i);
            items.add(s);
        }
    }



    private void InitFourerDGV(Seasonality season) {
        ObservableList<String> items = fourerLv.getItems();
        items.clear();

        items.add("A0\t" + season.a.get(0));

        for (int i = 0; i < season.b.size(); i++) {
            String str = "A" + (i + 1) + "\t" + season.a.get(i + 1);
            str += "\t\t";
            str += "B" + (i + 1) + "\t" + season.b.get(i);
            items.add(str);
        }
    }

    private void InitSeasonWaveDGV(Seasonality season) {
        ObservableList<String> items = seasonWaveLv.getItems();
        items.clear();

        for (int i = 0; i < season.seasonWave.size(); i++) {
            String str = (i + 1) + "\t" + season.seasonWave.get(i);
            items.add(str);
        }
    }

    private void InitForecastDGV(Seasonality season) {
        ObservableList<String> items = forecastLv.getItems();
        items.clear();

        int startIndx = dataSet.size();

        for (int i = 0; i < season.forecast.size(); i++) {
            String str = (startIndx + i + 1) + "\t" + season.forecast.get(i);
            items.add(str);
        }
    }


    @Override
    protected void buildTable(List<Double> dataSet) {
        super.buildTable(dataSet);

        List<ExistDto> characteristics = new ArrayList<>();


        Linear linear = new Linear(selectionSeasonalityWithTrendLinear);

        double fTest = linear.getFTest();
        double fisherQuantil = QuantilCalculator.FisherQuantil(1, dataSet.size() - 2);
        ExistDto fTestDto = new ExistDto("F тест",
                (float) fTest,
                (float) fisherQuantil,
                fTest > fisherQuantil ? "значимый" : "не значимый");


        float r2percent = LinearXmlController.round2(linear.getR2() * 100, 2);
        ExistDto r2Dto = new ExistDto(
                "R^2",
                r2percent+"%",
                100.0F,
                r2percent > 50 ? "адекватный" : "не адекватный");

        characteristics.add(new ExistDto("-----", 0f, 0f, "----"));
        characteristics.add(fTestDto);
        characteristics.add(r2Dto);

        charTable.getItems().addAll(characteristics);
    }
}
