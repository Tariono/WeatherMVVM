package data.repository

import androidx.lifecycle.LiveData
import data.database.entity.WeatherLocation
import data.database.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import data.database.unitlocalized.future.detail.UnitSpecificDetailFutureWeatherEntry
import data.database.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import org.threeten.bp.LocalDate

interface ForecastRepository {

    suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry>

    suspend fun getFutureWeatherList(startDate:LocalDate, metric: Boolean): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>>

    suspend fun getWeatherLocation(): LiveData<WeatherLocation>

    suspend fun getDetailedFutureWeather(date : LocalDate, metric: Boolean) : LiveData<out UnitSpecificDetailFutureWeatherEntry>

}