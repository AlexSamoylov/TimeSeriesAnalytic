package org.dnu.samoylov.controller.main;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.dnu.samoylov.controller.sub.linear.Linear;
import org.dnu.samoylov.controller.sub.seasonality.Seasonality;
import org.dnu.samoylov.util.SpringFXMLLoader;
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



    private Linear linear;
    private List<Double> dataSet;
    private List<Double> withoutTrendDataSet;

    public static void showSeasonality(Linear linear, List<Double> dataSet, List<Double> withoutTrendDataSet) {

        final Group root = new Group();
        final Stage stage = new Stage();

        final SeasonalityController controller = (SeasonalityController) SpringFXMLLoader.load("/fxml/seasonality.fxml");
        root.getChildren().add(controller.getView());
        final Scene scene = new Scene(root);

        stage.setScene(scene);
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

        List<Double> selectionSeasonalityWithTrendLinear = new ArrayList<>(dataSet.size());

        Integer period = Integer.valueOf(periodTF.getText());
        for (int i = 0; i < dataSet.size(); i++) {
            Double tmp_data = linear.calcU(i) + seasonalityLinear.seasonWave.get( i % period);
            selectionSeasonalityWithTrendLinear.add(tmp_data);
        }

        addNewLine(selectionSeasonalityWithTrendLinear, "бла");
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

}
