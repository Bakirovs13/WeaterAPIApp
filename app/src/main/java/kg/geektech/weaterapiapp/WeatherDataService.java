package kg.geektech.weaterapiapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    public static final String QUERY_FOR_CITY_WEATER_BY_ID = "https://www.metaweather.com/api/location/";
    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    Context context;
    String cityID;

    public WeatherDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener{

        void onError(String msg);

        void onResponse(String cityID);
    }


    public void getCityID(String cityName,VolleyResponseListener volleyResponseListener) {
        String url = QUERY_FOR_CITY_ID + cityName;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                 cityID = "";
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               // Toast.makeText(context, "City ID = " + cityID, Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(cityID);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(context, "That didn't work!", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something wrong");
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);
       // return cityID;
    }

    public interface ForeCastByIDResponse{

        void onError(String msg);

        void onResponse( List<WeatherReportModel>weatherReportModels );
    }


    public void getCityForecastByID(String cityId,ForeCastByIDResponse foreCastByIDResponse){
        List<WeatherReportModel> weatherReportModels = new ArrayList<>();
        String url =QUERY_FOR_CITY_WEATER_BY_ID+cityId;
        //get the gson object

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray consolidated_weather = response.getJSONArray("consolidated_weather");
                    for (int i = 0; i < consolidated_weather.length(); i++) {

                        WeatherReportModel one_day = new WeatherReportModel();

                        JSONObject first_day_from_api = (JSONObject) consolidated_weather.get(i);

                    one_day.setId(first_day_from_api.getInt("id"));
                    one_day.setWeather_state_name( first_day_from_api.getString("weather_state_name"));
                    one_day.setApplicable_date(first_day_from_api.getString("applicable_date"));
                    one_day.setMin_temp(first_day_from_api.getLong("min_temp"));
                    one_day.setMax_temp(first_day_from_api.getLong("max_temp"));
                    one_day.setThe_temp(first_day_from_api.getLong("the_temp"));
                    weatherReportModels.add(one_day);
                    }
                    foreCastByIDResponse.onResponse(weatherReportModels);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);

    }

    public  interface GetCityForecastByNameCallback{
        void onError(String msg);
        void onResponse(List<WeatherReportModel> weatherReportModels);
    }


    public  void getCityForecastByName (String cityName, GetCityForecastByNameCallback getCityForecastByNameCallback){

        getCityID(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String msg) {

            }

            @Override
            public void onResponse(String cityID) {
                getCityForecastByID(cityID, new ForeCastByIDResponse() {
                    @Override
                    public void onError(String msg) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                        //we have the weather report
                        getCityForecastByNameCallback.onResponse(weatherReportModels);
                    }
                });
            }
        });
    }

}
