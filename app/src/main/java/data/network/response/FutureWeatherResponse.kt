package data.network.response

import com.google.gson.annotations.SerializedName
import data.database.entity.WeatherLocation


data class FutureWeatherResponse(
    @SerializedName("forecast")
    val forecast: ForecastDaysContainer,
    val location: WeatherLocation
)