package data.network.response

import com.google.gson.annotations.SerializedName
import data.database.entity.CurrentWeatherEntry
import data.database.entity.WeatherLocation


data class CurrentWeatherResponse(

    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry,
    val location: WeatherLocation
)