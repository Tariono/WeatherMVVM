package data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.database.entity.FutureWeatherEntry
import data.database.unitlocalized.future.detail.ImperialDetailFutureWeatherEntry
import data.database.unitlocalized.future.detail.MetricDetailFutureWeatherEntry
import data.database.unitlocalized.future.list.ImperialSimpleFutureWeatherEntry
import data.database.unitlocalized.future.list.MetricSimpleFutureWeatherEntry
import org.threeten.bp.LocalDate


@Dao
interface FutureWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(futureWeatherEntries:List<FutureWeatherEntry>)

    @Query("select * from future_weather where date(date) >= date(:startDate)")
    fun getSimpleWeatherForecastsMetric(startDate: LocalDate) : LiveData<List<MetricSimpleFutureWeatherEntry>>

    @Query("select * from future_weather where date(date) >= date(:startDate)")
    fun getSimpleWeatherForecastsImperial(startDate: LocalDate) : LiveData<List<ImperialSimpleFutureWeatherEntry>>

    @Query("select count(id) from future_weather where date(date) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate) : Int

    @Query("delete from future_weather where date(date) < date(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep:LocalDate)

    @Query("select * from future_weather where date(date) = date(:date)")
    fun getDetailedWeatherForecastsMetric(date: LocalDate) : LiveData<MetricDetailFutureWeatherEntry>

    @Query("select * from future_weather where date(date) = date(:date)")
    fun getDetailedWeatherForecastsImperial(date: LocalDate) : LiveData<ImperialDetailFutureWeatherEntry>

}