package data.repository

import androidx.lifecycle.LiveData
import data.database.CurrentWeatherDao
import data.database.FutureWeatherDao
import data.database.WeatherLocationDao
import data.database.entity.WeatherLocation
import data.database.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import data.database.unitlocalized.future.detail.UnitSpecificDetailFutureWeatherEntry
import data.database.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import data.network.FORECAST_DAYS_COUNT
import data.network.WeatherNetworkDataSource
import data.network.response.CurrentWeatherResponse
import data.network.response.FutureWeatherResponse
import data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(
    private val currentWeatherDao : CurrentWeatherDao,
    private val futureWeatherDao: FutureWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider


) : ForecastRepository {

    init {
        weatherNetworkDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
                downloadedFutureWeather.observeForever { newFutureWeather ->
                    persistFetchedFutureWeather(newFutureWeather)
                }
            }
        }
    }

    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry>{
       return  withContext(Dispatchers.IO){
           initWeatherData()
           return@withContext if(metric) currentWeatherDao.getWeatherMetric()
           else  currentWeatherDao.getWeatherImperial()
           }
    }

    override suspend fun getFutureWeatherList(
        startDate: LocalDate,
        metric: Boolean
    ): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>> {
        return withContext(Dispatchers.IO){
            initWeatherData()
            return@withContext if(metric) futureWeatherDao.getSimpleWeatherForecastsMetric(startDate)
            else futureWeatherDao.getSimpleWeatherForecastsImperial(startDate)
        }
    }

    override suspend fun getDetailedFutureWeather(
        date: LocalDate,
        metric: Boolean
    ): LiveData<out UnitSpecificDetailFutureWeatherEntry> {
        return withContext(Dispatchers.IO){
            initWeatherData()
            return@withContext if(metric) futureWeatherDao.getDetailedWeatherForecastsMetric(date)
            else futureWeatherDao.getDetailedWeatherForecastsImperial(date)
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather : CurrentWeatherResponse) {
        GlobalScope.launch (Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse){

        fun deleteOldForecastData(){
            val today = LocalDate.now()
            futureWeatherDao.deleteOldEntries(today)
        }


        GlobalScope.launch(Dispatchers.IO) {
            deleteOldForecastData()
            val futureWeatherList = fetchedWeather.forecast.entries
            futureWeatherDao.insert(futureWeatherList)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private suspend fun  initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocationNonAlive()

        if (lastWeatherLocation == null ||
            locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            fetchFutureWeather()
            return
        }

        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
            fetchCurrentWeather()
            fetchFutureWeather()
        }


    private suspend fun fetchCurrentWeather(){
        weatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString(),
            Locale.getDefault().language
        )
    }

    private suspend fun fetchFutureWeather(){
        weatherNetworkDataSource.fetchFutureWeather(
            locationProvider.getPreferredLocationString(),
            Locale.getDefault().language
        )
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime) : Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }

    private fun isFetchFutureNeeded():Boolean {
        val today = LocalDate.now()
        val futureWeatherCount = futureWeatherDao.countFutureWeather(today)
        return futureWeatherCount < FORECAST_DAYS_COUNT
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO){
            return@withContext weatherLocationDao.getLocation()
        }
    }
}