import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeatherModel {

    /**
     * NOAA CDO token.
     * @see <a href="https://www.ncdc.noaa.gov/cdo-web/webservices/v2#gettingStarted">https://www.ncdc.noaa.gov/cdo-web/webservices/v2#gettingStarted</a>
     */

    private static final String TOKEN = "VpTOzPdWzMeZNxKkVgaiEBxjpqBreofz";

    /** Reference to the controller. */
    WeatherController controller;


    //Array for state names
    String[] states = new String[51];

    //Array of ArrayLists filled with Counties objects
    ArrayList[] cArray = new ArrayList[51];

    //The 2 arrays are synchronized so that the indexes match between a state and the states counties

    /**
     * Constructor. Loads data structures to get FIPS based on state name /
     * county name, and saves reference to the controller.
     *
     * @param controller WeatherController for the app.
     */
    public WeatherModel(WeatherController controller) {
        // save reference to the controller
        this.controller = controller;



        //Reading from counties.csv and placing into our synchronized arrays
        try {
            int i = -1;
            Scanner sc = new Scanner(new File("counties.csv"));
            sc.useDelimiter(",|\\n");   //sets the delimiter pattern
            while (sc.hasNext())  //returns a boolean value
            {
                String type=sc.next();
                String sIndex=sc.next();
                String cIndex=sc.next();
                String name=sc.next().trim();
                    if (type.equals("S")){
                        i++;
                        states[i] = name; // reads data onto states array
                        cArray[i] = new ArrayList<Counties>();
                    }else {
                        if (type.equals("C")){
                            cArray[i].add(new Counties(sIndex, cIndex, name));
                        }
                    }


            }
            sc.close();
        }catch(java.io.FileNotFoundException nfe){
            System.out.println("File not found");
    }
}

    /**
     * Get an array containing all of the state names, in alphabetical
     * order.
     *
     * @return String[] containing sorted state names.
     */
    public String[] getStates() {

        //Returns state Array creating during initialization
        return states;
    }

    /**
     * Get an array of all the counties in a given state, in alphabetical
     * order.
     *
     * @param state State to get counties for.
     *
     * @return String[] containing sorted county names.
     */
    public String[] getCounties(String state) {


        //Code to retrieve and save the index information for our synchronized arrays
        int i = 0;
        int stateIndex = 0;
        for (i=0; i<51; i++){
            if (state == states[i]){
                stateIndex = i;
                break;
            }
        }

        //Creating count for how many counties are in a given state
        int numCounties = cArray[stateIndex].size();
        //Creating a new array for the county names with how many counties are in a state as the size limiter
        String[] countyNames = new String[numCounties];

        //Use our synchronized arrays to get the array of county names to populate the drop down
        for (i=0; i<numCounties; i++){
            Counties county = (Counties)cArray[stateIndex].get(i);
            countyNames[i] = county.getcName();
        }

        return countyNames;

    }

    /**
     *
     * @param state Name of state.
     *
     * @param county Name of county.
     *
     * @return Corresponding FIPS code.
     */
    String getFIPS(String state, String county) {

        String fips = "";

        //finding the state index
        int i = 0;
        int stateIndex = 0;
        for (i=0; i<51; i++){
            if (state == states[i]){
                stateIndex = i;
                break;
            }
        }

        //Finding the total number of counties in a state
        int numCounties = cArray[stateIndex].size();

        //Retrieving the name from Counties object
        for (i=0; i<numCounties; i++){
            //Pulling out each County object, given stateIndex
            Counties countyObj = (Counties)cArray[stateIndex].get(i);
            //Checking to see if the county names match
            if (countyObj.getcName().equals(county)){
                //Building FIPS data
                fips = countyObj.getsNum() + countyObj.getcNum();
                System.out.println(fips);
            }
        }


        return fips;
    }

    /**
     *
     * @param state Name of the state to get data for.
     *
     * @param county Name of the county to get data for.
     *
     * @param startDate LocalDate somewhere within the month to fetch data for.
     */
    public void fetchData(String state, String county, LocalDate startDate) {


        try {
            //Grabbing full JSONObject from NOAA
            JSONObject data = NOAAReader.getTmaxData(TOKEN, getFIPS(state, county), startDate);

            //Creating JSONArray for results data
            JSONArray highTemps = data.getJSONArray("results");

            //Instantiating new temps array
            ArrayList<Double> temps = new ArrayList<Double>(highTemps.length());

            //Iterating through each {bracket} of data in highTemps JSONArray
            for (int i = 0; i < highTemps.length(); i++) {
                //Converting {bracket} info to JSONObject
                JSONObject hiTemp = highTemps.getJSONObject(i);
                //Reading value data from new JSONObject
                double temp = hiTemp.getDouble("value");
                //Convert to Fahrenheit and add to temps array
                temps.add(((temp / 10) * 1.8) + 32);
            }

            //Formatting date for LineGraph
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL yyyy");
            String textDate = startDate.format(formatter);

            //Printing out information to reference
            System.out.println(textDate);
            System.out.println(temps);

            //plotting temps and date in the GUI
            controller.plot(temps, "High temps for " + county + " " + textDate);

        }catch(JSONException json){
            //Running error message if the JSON data is not found
            controller.showError("There was an error finding data");
        }



    }

}
