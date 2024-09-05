package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val CITY: String = "baltimore,md"
    val API: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherTask().execute()
    }

    inner class weatherTask() : AsyncTask<String, Void, String?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response: String? = null
            try {
                val url = URL("https://api.openweathermap.org/data/3.0/onecall?lat=39.30&lon=-76.61&units=imperial&appid=$API")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val inputStream = conn.inputStream
                response = inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                Log.e("WeatherApp", "Error fetching weather data", e)
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {
                if (result != null) {
                    val jsonObj = JSONObject(result)
                    val current = jsonObj.getJSONObject("current")
                    val weather = current.getJSONArray("weather").getJSONObject(0)
                    val daily = jsonObj.getJSONArray("daily").getJSONObject(0)
                    val tempDaily = daily.getJSONObject("temp")
                    val updatedAt: Long = current.getLong("dt")
                    val updatedAtText = "Updated at " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US).format(Date(updatedAt * 1000))
                    val temp = current.getString("temp") + "°F"
                    val humidity = current.getString("humidity")
                    val sunrise: Long = current.getLong("sunrise")
                    val sunset: Long = current.getLong("sunset")
                    val windSpeed = current.getString("wind_speed")
                    val weatherDescription = weather.getString("description")
                    val lowTemp = "Low: " + String.format("%.2f°F", tempDaily.getDouble("min"))
                    val highTemp = "High: " + String.format("%.2f°F", tempDaily.getDouble("max"))

                    findViewById<TextView>(R.id.updated_at).text = updatedAtText
                    findViewById<TextView>(R.id.status).text = weatherDescription.replaceFirstChar { it.uppercase() }
                    findViewById<TextView>(R.id.temp).text = temp
                    findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                    findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                    findViewById<TextView>(R.id.wind).text = windSpeed
                    findViewById<TextView>(R.id.humidity).text = humidity
                    findViewById<TextView>(R.id.low_temp).text = lowTemp
                    findViewById<TextView>(R.id.high_temp).text = highTemp

                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                } else {
                    throw Exception("Result is null")
                }
            } catch (e: Exception) {
                Log.e("WeatherApp", "Error parsing weather data", e)
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }
    }
}
