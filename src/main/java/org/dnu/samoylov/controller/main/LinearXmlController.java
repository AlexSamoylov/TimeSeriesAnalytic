package org.dnu.samoylov.controller.main;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dnu.samoylov.controller.sub.linear.Linear;
import org.dnu.samoylov.controller.sub.linear.NonLinear;
import org.dnu.samoylov.service.estimate.QuantilCalculator;
import org.dnu.samoylov.util.SpringFXMLLoader;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class LinearXmlController extends MainController implements Initializable {
    public ListView<String> coefView;
    public ToggleButton typeChangingBtn;

    List<Double> dataSet;
    Linear linear;

    public static void showLinear(ActionEvent event, List<Double> dataSet) {

        final Group root = new Group();
        final Stage stage = new Stage();

        final LinearXmlController controller = (LinearXmlController) SpringFXMLLoader.load("/fxml/linear.fxml");
        root.getChildren().add(controller.getView());
        final Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());

        stage.show();

        if (dataSet != null) {
            controller.twerkWithDataSet(dataSet);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    @FXML
    public void typeChange(ActionEvent actionEvent) {
        if (typeChangingBtn.isSelected()) {
            typeChangingBtn.setText("u = 1 / (a0 + a1*t)");
        } else {
            typeChangingBtn.setText("u = a0 + a1*t");
        }
    }

    @Override
    protected void twerkWithDataSet(List<Double> dataSet) {
        this.dataSet = dataSet;
        linear = typeChangingBtn.isSelected()? new NonLinear(dataSet) : new Linear(dataSet);

        final List<Double> selectionWithoutTrendLinear = getWithoutTrendDataSet(dataSet);
        final List<Double> trendLinear = getTrendDataSet(dataSet);


        buildGist(dataSet);
        addNewLine(trendLinear, "тренд");
        buildTable(selectionWithoutTrendLinear);
        showCoef();

        buildCorrelogramChart(selectionWithoutTrendLinear);
    }


    public void showSeasonality(ActionEvent actionEvent) {
        SeasonalityController.showSeasonality(actionEvent, linear, dataSet, getWithoutTrendDataSet(dataSet));
    }


    private List<Double> getTrendDataSet(List<Double> dataSet) {
        final List<Double> selectionWithoutTrendLinear = new ArrayList<>(dataSet.size());

        for (int i = 0; i < dataSet.size(); i++) {
            Double tmp_data = linear.calcU(i + 1);
            selectionWithoutTrendLinear.add(tmp_data);
        }

        return selectionWithoutTrendLinear;
    }


    private List<Double> getWithoutTrendDataSet(List<Double> dataSet) {
        final List<Double> selectionWithoutTrendLinear = new ArrayList<>(dataSet.size());

        for (int i = 0; i < dataSet.size(); i++) {
            Double tmp_data = dataSet.get(i) - linear.calcU(i + 1);
            selectionWithoutTrendLinear.add(tmp_data);
        }
        return selectionWithoutTrendLinear;
    }

    private void showCoef() {
        coefView.getItems().clear();
        coefView.getItems().addAll(
                "a0= " + linear.getA(),
                "a1= " + linear.getB()
        );
    }

    @Override
    protected void buildTable(List<Double> dataSet) {
        List<ExistDto> characteristics = new ArrayList<>();

        double fTest = linear.getFTest();
        double fisherQuantil = QuantilCalculator.FisherQuantil(1, dataSet.size() - 2);
        ExistDto fTestDto = new ExistDto("F тест",
                (float) fTest,
                (float) fisherQuantil,
                fTest > fisherQuantil ? "значимый" : "не значимый");


        float r2percent = round2(linear.getR2() * 100, 2);
        ExistDto r2Dto = new ExistDto(
                "R^2",
                r2percent+"%",
                100.0F,
                r2percent > 50 ? "адекватный" : "не адекватный");

        characteristics.add(fTestDto);
        characteristics.add(r2Dto);

        charTable.setItems(FXCollections.observableList(characteristics));
    }

    public static float round2(double number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        double tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

}
