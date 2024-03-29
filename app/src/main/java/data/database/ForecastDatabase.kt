package data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import data.database.entity.CurrentWeatherEntry
import data.database.entity.FutureWeatherEntry
import data.database.entity.WeatherLocation


@Database(
    entities = [CurrentWeatherEntry::class,FutureWeatherEntry::class, WeatherLocation::class],
    version = 1
)

@TypeConverters(LocalDateConverter::class)
abstract class ForecastDatabase : RoomDatabase() {
    abstract fun currentWeatherDao() : CurrentWeatherDao
    abstract fun futureWeatherDao() : FutureWeatherDao
    abstract fun currentLocationDao() : WeatherLocationDao

    companion object {
        @Volatile private var instance : ForecastDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {instance = it}
        }

        private fun buildDatabase (context: Context) =
                Room.databaseBuilder(context.applicationContext,
                    ForecastDatabase::class.java,
                    "forecast.db")
                    .build()
    }
}