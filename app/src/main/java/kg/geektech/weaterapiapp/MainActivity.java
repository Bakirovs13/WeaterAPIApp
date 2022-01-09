package kg.geektech.weaterapiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kg.geektech.weaterapiapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
  final   WeatherDataService service = new WeatherDataService(MainActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initListeners();

    }

    private void initListeners() {
        binding.WeatherbyIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                service.getCityForecastByID(binding.dataInputEt.getText().toString() , new WeatherDataService.ForeCastByIDResponse() {
                    @Override
                    public void onError(String msg) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {

                        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,weatherReportModels);
                        binding.lvWeatherReports.setAdapter(adapter);
                    }
                });
            }
        });


        binding.getCityidBtn.setOnClickListener(view -> {
           service.getCityID(binding.dataInputEt.getText().toString(), new WeatherDataService.VolleyResponseListener() {
                @Override
                public void onError(String msg) {
                    Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String cityID) {
                    Toast.makeText(MainActivity.this, "Returned a iD of "+cityID, Toast.LENGTH_SHORT).show(); 
                }
            });

        });

        binding.weatherbyName.setOnClickListener(view -> {

            service.getCityForecastByName(binding.dataInputEt.getText().toString() , new WeatherDataService.GetCityForecastByNameCallback() {
                @Override
                public void onError(String msg) {

                }

                @Override
                public void onResponse(List<WeatherReportModel> weatherReportModels) {

                    ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,weatherReportModels);
                    binding.lvWeatherReports.setAdapter(adapter);
                }
            });

        });


    }

}