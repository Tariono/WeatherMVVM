package data.network.response

import com.google.gson.annotations.SerializedName
import data.database.entity.FutureWeatherEntry


data class ForecastDaysContainer(
    @SerializedName("forecastday")
    val entries: List<FutureWeatherEntry>
)