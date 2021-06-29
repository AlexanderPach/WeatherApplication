import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

/**
 * Class providing a connection to the NOAA National Centers for Environmental Information
 * Climate Data Online (CDO) site. Provided with the correct data, the static getTmaxData()
 * method will return a JSON object with a month's worth of maximum temperature data.
 *
 * DO NOT MODIFY THIS CLASS.
 *
 */
public class NOAAReader {

    /**
     * Base URL for the request.
     */
    private static final String BASE_URL = "https://www.ncdc.noaa.gov/cdo-web/api/v2/data?datasetid=GHCND&datatypeid=TMAX&";

    /**
     * Given a LocalDate, return a String version.
     *
     * @param ld LocalDate object.
     *
     * @return String version of the date, as "YYYY-MM-DD"
     */
    private static String getDateString(LocalDate ld) {
        return ld.getYear() + "-" +
                String.format("%02d", ld.getMonth().getValue()) + "-" +
                String.format("%02d", ld.getDayOfMonth());
    }

    /**
     * Get a JSON object from the NOAA CDO stie, containing daily maximum temperatures, based on these parameters.
     *
     * @param token     Access token. Request a token online.
     * @param FIPS      Federal Information Processing Standards (FIPS) code for the desired county.
     * @param startDate LocalDate within the month to fetch data for.
     * @return A JSON object with all the maximum daily temperature reports for the county and month specified.
     * @see <a href="https://www.ncdc.noaa.gov/cdo-web/webservices/v2#gettingStarted">https://www.ncdc.noaa.gov/cdo-web/webservices/v2#gettingStarted</a>
     */
    public static JSONObject getTmaxData(String token, String FIPS, LocalDate startDate) {
        // String for building the JSON object
        String json = "";

        // get first and last date of selected month
        startDate = startDate.withDayOfMonth(1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // complete URL for the query
        String path = BASE_URL + "limit=" + startDate.lengthOfMonth() +
                "&datatypeid=TMAX" +
                "&locationid=FIPS:" + FIPS +
                "&startdate=" + getDateString(startDate) +
                "&enddate=" + getDateString(endDate);

        try {
            // connect to the server
            URL url = new URL(path);
            HttpURLConnection myUrlConnection = (HttpURLConnection) url.openConnection();

            // header settings for the request
            myUrlConnection.setRequestProperty("Token", token);
            myUrlConnection.setRequestMethod("GET");
            myUrlConnection.setRequestProperty("Accept", "application/json");

            // did the connection work?
            if (myUrlConnection.getResponseCode() != 200) {
                // if there was an HTTP error, send back an error JSON object
                return new JSONObject("{\"error\":\"HTTP error " +
                        myUrlConnection.getResponseCode() + "\"}");
            }

            // read JSON string from NOAA
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    myUrlConnection.getInputStream()));
            String s = null;
            while ((s = br.readLine()) != null) {
                json += s;
            }

            // hang up
            myUrlConnection.disconnect();

        } catch (Exception e) {
            // on an exception, send back an error JSON object
            return new JSONObject("{\"error\":\"" +
                    e.getMessage() + "\"}");
        }

        // if there was no data from NOAA, send back an
        // error JSON object
        if (json.equals("{}")) {
            json = "{\"error\":\"empty JSON object\"}";
        }

        // send the JSON object back
        System.out.println(json.toString());
        return new JSONObject(json);
    }
}
