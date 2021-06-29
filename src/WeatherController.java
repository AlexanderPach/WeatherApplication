import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;


public class WeatherController {
    /** State combo box */
    @FXML private ComboBox<String> states;

    /** County combo box */
    @FXML private ComboBox<String> counties;

    /** Month date picker */
    @FXML private DatePicker startDate;

    /** Fetch button */
    @FXML private Button fetchButton;

    /** Line chart for displaying temperatures */
    @FXML private LineChart<String, Double> chart;

    /** Selection model for states */
    private SingleSelectionModel<String> stateSelectionModel;

    /** Selection model for counties */
    private SingleSelectionModel<String> countySelectionModel;

    /** Reference to the model */
    private WeatherModel model;

    /**
     * Called to when app is instantiated.
     */
    public void initialize() {
        // create model, linking it to this controller
        model = new WeatherModel(this);

        // populate the state and county combo boxes
        states.getItems().addAll(model.getStates());
        stateSelectionModel = states.getSelectionModel();
        stateSelectionModel.select(0);

        counties.getItems().addAll(model.getCounties("Alabama"));
        countySelectionModel = counties.getSelectionModel();
        countySelectionModel.select(0);

        // set the date picker to today's date
        startDate.setValue(LocalDate.now());
    }

    /**
     * Event handling method for the state combo box.
     *
     * @param event Event generated when a state is selected
     */
    @FXML protected void stateSelected(ActionEvent event) {
        // get state selected
        String state = stateSelectionModel.getSelectedItem();

        // repopulate counties based on state
        counties.getItems().clear();
        counties.getItems().addAll(model.getCounties(state));
        countySelectionModel.select(0);
    }

    /**
     * Event handler for the Fetch button
     *
     * @param event Event generated when the button is clicked
     */
    @FXML protected void fetchData(ActionEvent event) {
        //Running fetchData method based on which state, county, and date was selected
        model.fetchData(stateSelectionModel.getSelectedItem(), countySelectionModel.getSelectedItem(), startDate.getValue());
    }

    /**
     * Method called by the model to draw temperatures on the line chart.
     *
     * @param temps List of temperatures for the selected State,
     *              County, and month
     *
     * @param date String with date for the plot, like "MARCH, 2018"
     */
    public void plot(List<Double> temps, String date) {

        //Creating new data series for LineChart
        XYChart.Series series = new XYChart.Series();

        //Setting date for LineChart
        series.setName(date);

        //Filling series with X, Y points based on index and temps data
        for (int i=0; i<temps.size(); i++){
            series.getData().add(new XYChart.Data(i, temps.get(i)));
        }

        //adding the data series to the chart
        chart.getData().add(series);

    }

    /**
     * Method called by the model if there is an error getting the temperature
     * data.
     *
     * @param msg Error message to display
     */
    public void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK);
    }
}
