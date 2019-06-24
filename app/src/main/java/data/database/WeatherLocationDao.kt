package data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.database.entity.WEATHER_LOCATION_ID
import data.database.entity.WeatherLocation


@Dao
interface WeatherLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherLocation: WeatherLocation)

    @Query("select * from weather_location where id = $WEATHER_LOCATION_ID")
    fun getLocation() : LiveData<WeatherLocation>

    @Query("select * from weather_location where id = $WEATHER_LOCATION_ID")
    fun getLocationNonAlive() : WeatherLocation?

}