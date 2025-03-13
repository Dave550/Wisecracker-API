package edu.citadel.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.json.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

@RestController
@RequestMapping("/weather")
public class WeatherEndpoints {
    // establish rest controller for all http requests
    RestTemplate restTemplate = new RestTemplate();

    @GetMapping(value = "/station", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getStationData() throws IOException {
        Document doc = Jsoup.connect("https://www.wunderground.com/dashboard/pws/KSCMOUNT220").get();
        String temp = doc.body().selectXpath("//div[@class='main-temp']//span[contains(@class, 'wu-value')]").text();

        JSONObject response = new JSONObject().put("temperature", temp);

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/{state}/{city}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCityForecast(@PathVariable String state, @PathVariable String city) {
        // establish variables
        JSONArray response = new JSONArray();
        HttpStatus status = HttpStatus.NOT_FOUND;
        JSONArray forecasts = getForecasts(state, city);

        // if state and county produce valid coordinates
        if (forecasts != null) {
            status = HttpStatus.OK;

            for (int i = 0; i < forecasts.length(); i++) {
                JSONObject forecastItem = forecasts.getJSONObject(i);

                // record forecast information
                JSONObject forecast = new JSONObject();
                forecast.put("timeFrame", forecastItem.getString("name"));
                forecast.put("forecast", forecastItem.getString("detailedForecast"));
                forecast.put("temperature", forecastItem.getInt("temperature"));
                forecast.put("temperatureUnit", forecastItem.getString("temperatureUnit"));
                forecast.put("windDirection", forecastItem.getString("windDirection"));
                forecast.put("windSpeed", forecastItem.getString("windSpeed"));

                // add new forecast to response
                response.put(forecast);
            }
        }

        // send response to user as string with http status
        return new ResponseEntity<>(response.toString(), status);
    }

    @GetMapping(value = "/{state}/{city}/hurricane", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getHurricane(@PathVariable String state, @PathVariable String city) {
        // initialize variables
        JSONObject response = new JSONObject();
        HttpStatus status = HttpStatus.NOT_FOUND;
        JSONArray forecasts = getForecasts(state, city);

        // if state and city produce valid coordinates
        if (forecasts != null) {
            status = HttpStatus.OK;

            // initialize variables
            String windStatus = "No Hurricane Force Winds";
            int category = 0;

            // retrieve wind speed from most current forecast
            String windSpeedText = forecasts.getJSONObject(0).getString("windSpeed");
            int windSpeed = Integer.parseInt(windSpeedText.split(" ")[0]);

            // check wind speed against thresholds for various hurricane category levels
            if (windSpeed > 73) {
                windStatus = "Hurricane Force Winds Present";

                if (windSpeed > 156) {
                    category = 5;
                }
                else if (windSpeed > 129) {
                    category = 4;
                }
                else if (windSpeed > 110) {
                    category = 3;
                }
                else if (windSpeed > 95) {
                    category = 2;
                }
                else {
                    category = 1;
                }
            }

            // add wind data to response
            response.put("status", windStatus);
            response.put("category", category);
        }

        // send response to user as string with http status
        return new ResponseEntity<>(response.toString(), status);
    }

    @GetMapping(value = "/{state}/{city}/temperature", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getTemperature(@PathVariable String state, @PathVariable String city) {
        // initialize variables
        JSONObject response = new JSONObject();
        HttpStatus status = HttpStatus.NOT_FOUND;
        JSONArray forecasts = getForecasts(state, city);

        // if state and city produce valid coordinates
        if (forecasts != null) {
            status = HttpStatus.OK;

            // initialize variables
            String warning = "No Temperature Warnings";

            // retrieve temperature data from most current forecast
            int temperature = forecasts.getJSONObject(0).getInt("temperature");
            String temperatureUnit = forecasts.getJSONObject(0).getString("temperatureUnit");
            String temperatureString = String.format("%d%s", temperature, temperatureUnit);

            // check temperature against thresholds for various temperature extremes
            if (temperature > 109) {
                warning = "High Heat Warning";
            }
            else if (temperature > 89) {
                warning = "Moderate Heat Warning";
            }
            else if (temperature < 33) {
                warning = "Freezing Cold Warning";
            }

            // add temperature data to response
            response.put("temperature", temperatureString);
            response.put("warning", warning);
        }

        // send response to user as string with http status
        return new ResponseEntity<>(response.toString(), status);
    }

    public JSONArray getForecasts(String state, String city) {
        JSONArray forecasts = null;

        // retrieve coordinates for city and state
        String uri = String.format("https://www.mapquestapi.com/geocoding/v1/address?key=nWoaQsV4ILM0TV4mjDqDxxYTVlKSDEJb&location=%s,%s", city, state);
        JSONObject locationsReq = new JSONObject(restTemplate.getForObject(uri, String.class));

        // parse to retrieve coordinates
        JSONObject coordinates = locationsReq.getJSONArray("results").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getJSONObject("latLng");

        if (coordinates != null) {
            // retrieve forecast endpoint for coordinates
            uri = String.format("https://api.weather.gov/points/%s,%s", coordinates.get("lat"), coordinates.get("lng"));
            JSONObject pointReq = new JSONObject(restTemplate.getForObject(uri, String.class));

            // retrieve forecasts from forecast endpoint
            uri = pointReq.getJSONObject("properties").getString("forecast");
            JSONObject forecastsReq = new JSONObject(restTemplate.getForObject(uri, String.class));

            // parse to retrieve forecasts
            forecasts = forecastsReq.getJSONObject("properties").getJSONArray("periods");
        }

        return forecasts;
    }

        // ALTERNATIVE CODE FOR GETTING FORECASTS THROUGH FINDING ZONE ID

//    @GetMapping(value = "/{state}/{county}/forecast", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<String> getForecast(@PathVariable String state, @PathVariable String county) {
//        // establish variables
//        JSONArray response = new JSONArray();
//        HttpStatus status = HttpStatus.NOT_FOUND;
//        String zoneID = getZoneID(state, county);
//
//        // if zoneID exists for state and county...
//        if (zoneID != null) {
//            status = HttpStatus.OK;
//            String uri = String.format("https://api.weather.gov/zones/forecast/%s/forecast", zoneID);
//
//            // execute GET request and parse response to get to list of forecasts
//            JSONObject forecastsReq = new JSONObject(restTemplate.getForObject(uri, String.class));
//            JSONArray forecasts = forecastsReq.getJSONObject("properties").getJSONArray("periods");
//
//            // for each forecast...
//            for (int i = 0; i < forecasts.length(); i++) {
//                JSONObject forecastItem = forecasts.getJSONObject(i);
//
//                // parse information of forecast
//                String timeFrame = forecastItem.getString("name");
//                String details = forecastItem.getString("detailedForecast");
//
//                // add information to new json object
//                JSONObject forecast = new JSONObject();
//                forecast.put("timeFrame", timeFrame);
//                forecast.put("forecast", details);
//
//                // add json object to json array
//                response.put(forecast);
//            }
//        }
//
//        // send response to user as string with http status
//        return new ResponseEntity<>(response.toString(), status);
//    }
//
//    public String getZoneID(String state, String county) {
//        String zoneID = null;
//        String uri = String.format("https://api.weather.gov/zones/forecast?area=%s", state);
//
//        JSONObject zonesReq = new JSONObject(restTemplate.getForObject(uri, String.class));
//        JSONArray zones = zonesReq.getJSONArray("features");
//        for (int i = 0; i < zones.length(); i++) {
//            JSONObject zone = zones.getJSONObject(i).getJSONObject("properties");
//            String name = zone.getString("name");
//
//            if (name.equals(county)) {
//                zoneID = zone.getString("id");
//                break;
//            }
//        }
//
//        return zoneID;
//    }
}