package visualizealgorithms.gui.controller;

//Java imports

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

//Project imports
import visualizealgorithms.AlgorithmLoader;
import visualizealgorithms.bll.DataType;
import visualizealgorithms.bll.TaskRunner;
import visualizealgorithms.util.DataGenerator;
import visualizealgorithms.gui.model.DataPoint;
import visualizealgorithms.bll.algorithm.*;


/**
 * @author Søren Spangsberg
 */
public class MainWindowController implements Initializable {
    @FXML
    private TitledPane tpSettings;
    @FXML
    private TableColumn<DataPoint, Long> colInput = new TableColumn<>();
    @FXML
    private TableColumn<DataPoint, Long> colRunTime = new TableColumn<>();
    @FXML
    private RadioButton rbBEEntities;
    @FXML
    private RadioButton rbIntegers;
    @FXML
    private TableView<DataPoint> tbvRunTimes = new TableView<>();
    @FXML
    private Button btnStartAction;
    @FXML
    private ListView lvAlgorithms;
    @FXML
    private AnchorPane rightAnchorPane;
    @FXML
    private TextField txtInputs;
    @FXML
    private ProgressBar pbProgress;

    private ArrayList<Integer> inputRanges = new ArrayList();
    private ObservableList<DataPoint> runTimeData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        AlgorithmLoader loader = new AlgorithmLoader();

        lvAlgorithms.getItems().addAll(loader.getAlgorithms());
        txtInputs.setText(loader.getInputs());

        //initialize tableview
        colRunTime.setCellValueFactory(new PropertyValueFactory<DataPoint, Long>("runTime"));
        colInput.setCellValueFactory(new PropertyValueFactory<DataPoint, Long>("input"));
        tbvRunTimes.setItems(runTimeData);
        tbvRunTimes.setEditable(true);

        //rbBEEntities.setDisable(true);

        //only enable button if user clicked an algorithm
        lvAlgorithms.setOnMouseClicked((event) -> {
            btnStartAction.setDisable(lvAlgorithms.getSelectionModel().getSelectedIndex() == -1);
        });
    }

    /**
     * Creates a LineChart to hold the registered data values
     *
     * @return
     */
    private LineChart<Number, Number> buildChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("# of elements");
        yAxis.setLabel("ms");

        //creating the chart
        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setLegendVisible(false);

        //setup chart to fit parent (anchor pane)
        AnchorPane.setTopAnchor(lineChart, 0d);
        AnchorPane.setLeftAnchor(lineChart, 0d);
        AnchorPane.setRightAnchor(lineChart, 0d);
        AnchorPane.setBottomAnchor(lineChart, 0d);

        return lineChart;
    }

    /**
     * Creates a tooltip for the provided xyPlot data entry
     *
     * @param xyPlot
     */
    private void createToolTip(XYChart.Data<Number, Number> xyPlot) {
        String msg = "Input size: " + xyPlot.getXValue().toString() + "\n" + "Time: " + xyPlot.getYValue() + "ms";
        Tooltip tp = new Tooltip(msg);
        tp.setShowDelay(Duration.seconds(0));
        Tooltip.install(xyPlot.getNode(), tp);

        xyPlot.getNode().setOnMouseEntered(mEvent -> xyPlot.getNode().getStyleClass().add("onHover"));
        xyPlot.getNode().setOnMouseExited(mEvent -> xyPlot.getNode().getStyleClass().remove("onHover"));
    }

    /**
     * Do action and build line chart with result
     *
     * @param event
     */
    @FXML
    private void handleStartAction(ActionEvent event) {
        btnStartAction.setDisable(true);
        btnStartAction.setText("Running...");
        tpSettings.setExpanded(false);
        IAlgorithm selectedAlgorithm = (IAlgorithm) lvAlgorithms.getSelectionModel().getSelectedItem();
        DataType selectedDataType = rbIntegers.isSelected() ? DataType.Integer : DataType.User;
        DataGenerator ng = DataGenerator.getInstance();
        TaskRunner tr = new TaskRunner();

        getInputRangeFromGUI(); //get user defined input
        pbProgress.setProgress(0.0);

        if (selectedAlgorithm != null) {

            LineChart<Number, Number> lineChart = buildChart();
            XYChart.Series<Number, Number> series = new XYChart.Series<>();

            //Start the algorithm and measure the time
            Thread t = new Thread(() -> {

                for (int i = 0; i < runTimeData.size(); i++) {
                    //for (int i = 0; i < inputRanges.size(); i++) {

                    DataPoint dp = runTimeData.get(i);
                    Comparable[] data = null;
                    //int[] data = null;
                    final double value = (float) (i + 1) / (float) runTimeData.size();
                    final int numberOfInputs = dp.getInput();

                    switch (selectedAlgorithm.getType()) {
                        case SORTING:
                            data = ng.generateTestData(selectedDataType, numberOfInputs);
                            //data = ng.generateRandomIntegers(numberOfInputs, 10);
                            break;
                        case SEARCHING:
                            break;
                        case MISC:
                            data = new Integer[]{inputRanges.get(i)};
                            break;
                    }

                    final long durationInMillis = tr.runTask(selectedAlgorithm, data);

                    Platform.runLater(() -> {
                        //create and setup XY plot
                        XYChart.Data<Number, Number> xyPlot = new XYChart.Data(numberOfInputs, durationInMillis);
                        series.getData().add(xyPlot);
                        createToolTip(xyPlot);
                        pbProgress.setProgress(value);

                        dp.setRunTime(durationInMillis);
                    });
                }
                Platform.runLater(() -> {
                    btnStartAction.setText("Start");
                    btnStartAction.setDisable(false);
                });
            });
            t.start();

            lineChart.getData().add(series);

            rightAnchorPane.getChildren().clear();
            rightAnchorPane.getChildren().add(lineChart);
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        System.exit(0);
    }

    /**
     *
     */
    private void getInputRangeFromGUI() {
        String text = txtInputs.getText();

        if (text.trim().length() > 0) {
            this.runTimeData.clear();

            String[] inputRange = text.split(";");
            for (int i = 0; i < inputRange.length; i++) {
                // this.inputRanges.add(Integer.parseInt(inputRange[i]));
                this.runTimeData.add(new DataPoint(Integer.parseInt(inputRange[i])));
            }
        }
    }
}
