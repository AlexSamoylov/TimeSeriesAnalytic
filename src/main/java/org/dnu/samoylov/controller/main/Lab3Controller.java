package org.dnu.samoylov.controller.main;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dnu.samoylov.controller.sub.lab3.BoxDjenkinsModel;
import org.dnu.samoylov.controller.sub.lab3.ExponentialMultiplyModel;
import org.dnu.samoylov.controller.sub.lab3.LinearAdaptiveModel;
import org.dnu.samoylov.util.SpringFXMLLoader;
import org.dnu.samoylov.util.ZoomedLineChart;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Controller
public class Lab3Controller extends AbstractFxmlController implements Initializable {

    public static final String BOX_JENKINS_METHOD = "Box–Jenkins method";
    public static final String EXPONENTIAL_MULTIPLY = "Exponential Multiply";
    public static final String LINEAR_ADAPTIVE = "Linear Adaptive";
    public TableView<SmoothingDto> charSmoothingTable;
    public TableColumn smOrigValueColumn;
    public TableColumn smSmoothValue1Column;
    public TableColumn smDeviation1Column;
    public TableColumn smSmoothValue2Column;
    public TableColumn smDeviation2Column;
    public TableColumn col6;

    public TextField b1V;
    public TextField b2V;
    public TextField b3V;
    public TextField tV;
    public TextField lV;
    public TextField kV;
    public ComboBox<String> comboBox;

    @FXML
    private ZoomedLineChart empFunctionChart;

    List<Double> dataSet;

    public static void showLab3(ActionEvent event,List<Double> dataSet) {

        final Group root = new Group();
        final Stage stage = new Stage();

        final Lab3Controller controller = (Lab3Controller) SpringFXMLLoader.load("/fxml/lab3.fxml");
        root.getChildren().add(controller.getView());
        final Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());

        stage.show();

        if (dataSet != null) {
            controller.dataSet = dataSet;
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        empFunctionChart.setCreateSymbols(false);
        empFunctionChart.setAnimated(false);

        smOrigValueColumn.setCellValueFactory(new PropertyValueFactory("original"));
        smSmoothValue1Column.setCellValueFactory(new PropertyValueFactory("smooth1"));
        smDeviation1Column.setCellValueFactory(new PropertyValueFactory("deviation1"));
        smSmoothValue2Column.setCellValueFactory(new PropertyValueFactory("smooth2"));
        smDeviation2Column.setCellValueFactory(new PropertyValueFactory("deviation2"));
        col6.setCellValueFactory(new PropertyValueFactory("col6"));

        comboBox.getItems().addAll(BOX_JENKINS_METHOD, EXPONENTIAL_MULTIPLY, LINEAR_ADAPTIVE);
    }


    protected void buildGist( List<Double> dataSet) {
        empFunctionChart.getData().clear();
        empFunctionChart.layout();
        addNewLine(dataSet, "початковий");
    }

    protected void addNewLine(List<Double> dataSet, String name) {
        addNewLine(dataSet, name, 0);
    }

    protected void addNewLine(List<Double> dataSet, String name, int startPosition) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (int i = 0; i < dataSet.size(); i++) {
            series.getData().add( new XYChart.Data<>(i + startPosition, dataSet.get(i)) );
        }

        series.setName(name);

        empFunctionChart.getData().add(series);
        empFunctionChart.resetZoom();
    }


    @FXML
    private void button5_Click() {
        buildGist(dataSet);

        String selectedItem = comboBox.getSelectionModel().getSelectedItem();
        if (Objects.equals(selectedItem, BOX_JENKINS_METHOD)) {
            BoxDjenkinsModel model = new BoxDjenkinsModel(dataSet,
                    Double.valueOf(b1V.getText()), Double.valueOf(b2V.getText()), Double.valueOf(b3V.getText()),
                    Integer.valueOf(kV.getText()), Integer.valueOf(tV.getText()));
            model.CalculateModel();
            PrintModel(model);

            addNewLine(model.U, selectedItem);

        }
        if (Objects.equals(selectedItem, EXPONENTIAL_MULTIPLY)) {
            ExponentialMultiplyModel model = new ExponentialMultiplyModel(dataSet,
                    Double.valueOf(b1V.getText()), Double.valueOf(b2V.getText()), Double.valueOf(b3V.getText()),
                    Integer.valueOf(kV.getText()),
                    Integer.valueOf(lV.getText()), Integer.valueOf(tV.getText()));
            model.CalculateModel();
            PrintExponentialModel(model);
            addNewLine(model.U, selectedItem);
        }
        if (Objects.equals(selectedItem, LINEAR_ADAPTIVE)) {
            LinearAdaptiveModel model = new LinearAdaptiveModel(dataSet,
                    Double.valueOf(b1V.getText()), Double.valueOf(b2V.getText()), Double.valueOf(b3V.getText()),
                    Integer.valueOf(kV.getText()),
                    Integer.valueOf(lV.getText()), Integer.valueOf(tV.getText()));
            model.CalculateModel();
            PrintLinearModel(model);
            addNewLine(model.U, selectedItem);
        }
    }


    void PrintModel(BoxDjenkinsModel model)
    {
        ObservableList<SmoothingDto> items = charSmoothingTable.getItems();
        items.clear();
        ObservableList<TableColumn<SmoothingDto, ?>> columns = charSmoothingTable.getColumns();
        columns.get(0).setText("Ut");
        columns.get(1).setText("a1t");
        columns.get(2).setText("a2t");
        columns.get(3).setText("yt");
        columns.get(4).setText("et");
        columns.get(5).setText("");

        for (int i = 0; i < dataSet.size(); i++)
        {
            SmoothingDto dto = new SmoothingDto(
                    "" + dataSet.get(i),
                    "" + model.a1.get(i),
                    "" + model.a2.get(i),
                    "" + model.U.get(i)
                    , "" + model.e.get(i)
                    , "");
            items.add(dto);
        }

        for (int i = dataSet.size(); i < model.U.size(); i++)
        {
            SmoothingDto dto = new SmoothingDto(
                    "" , "" , ""
                    , "" + model.U.get(i)
                    , "", "");
            items.add(dto);
        }
    }

    void PrintExponentialModel(ExponentialMultiplyModel model)
    {
        ObservableList<SmoothingDto> items = charSmoothingTable.getItems();
        items.clear();
        ObservableList<TableColumn<SmoothingDto, ?>> columns = charSmoothingTable.getColumns();

        columns.get(0).setText("Ut");
        columns.get(1).setText("a1t");
        columns.get(2).setText("rt");
        columns.get(3).setText("ft");
        columns.get(4).setText("yt");
        columns.get(5).setText("et");


        for (int i = 0; i < dataSet.size(); i++)
        {
            SmoothingDto dto = new SmoothingDto(
                    "" + dataSet.get(i), "" + model.a1.get(i), "" + model.r.get(i)
                    , "" + model.f.get(i)
                    , "" + model.U.get(i), "" + model.e.get(i));
            items.add(dto);
        }

        for (int i = dataSet.size(); i < model.U.size(); i++)
        {
            SmoothingDto dto = new SmoothingDto(
                    "" , "" , ""
                    , "" + model.U.get(i)
                    , "", "");
            items.add(dto);
        }
    }

    void PrintLinearModel(LinearAdaptiveModel model)
    {
        ObservableList<SmoothingDto> items = charSmoothingTable.getItems();
        items.clear();
        ObservableList<TableColumn<SmoothingDto, ?>> columns = charSmoothingTable.getColumns();

        columns.get(0).setText("Ut");
        columns.get(1).setText("a1t");
        columns.get(2).setText("a2t");
        columns.get(3).setText("gt");
        columns.get(4).setText("yt");
        columns.get(5).setText("et");

        for (int i = 0; i < dataSet.size(); i++)
        {
            SmoothingDto dto = new SmoothingDto(
                    "" + dataSet.get(i), "" + model.a1.get(i), "" + model.a2.get(i)
                    , "" + model.g.get(i)
                    ,  "" + model.U.get(i), "" + model.e.get(i));
            items.add(dto);
        }

        for (int i = dataSet.size(); i < model.U.size(); i++)
        {
            SmoothingDto dto = new SmoothingDto(
                    "" , "" , ""
                    , "" + model.U.get(i)
                    , "", "");
            items.add(dto);
        }
    }
    

    public static class SmoothingDto {
        private String original;
        private String smooth1;
        private String deviation1;
        private String smooth2;
        private String deviation2;
        private final String col6;



        public SmoothingDto(String original, String smooth1, String deviation1, String smooth2, String deviation2, String col6) {
            this.original = original;
            this.smooth1 = smooth1;
            this.deviation1 = deviation1;
            this.smooth2 = smooth2;
            this.deviation2 = deviation2;
            this.col6 = col6;
        }


        public String getCol6() {
            return col6;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

        public String getSmooth1() {
            return smooth1;
        }

        public void setSmooth1(String smooth1) {
            this.smooth1 = smooth1;
        }

        public String getDeviation1() {
            return deviation1;
        }

        public void setDeviation1(String deviation1) {
            this.deviation1 = deviation1;
        }

        public String getSmooth2() {
            return smooth2;
        }

        public void setSmooth2(String smooth2) {
            this.smooth2 = smooth2;
        }

        public String getDeviation2() {
            return deviation2;
        }

        public void setDeviation2(String deviation2) {
            this.deviation2 = deviation2;
        }
    }

}
