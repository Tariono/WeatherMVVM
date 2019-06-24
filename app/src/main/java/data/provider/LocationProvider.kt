package data.provider

import data.database.entity.WeatherLocation

interface LocationProvider {
    suspend fun hasLocationChanged(LastWeatherLocation : WeatherLocation) : Boolean
    suspend fun getPreferredLocationString(): String
}