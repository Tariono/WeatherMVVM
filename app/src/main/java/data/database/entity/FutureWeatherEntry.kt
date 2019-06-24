package data.database.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "future_weather", indices = [Index(value=["date"], unique = true)])
data class FutureWeatherEntry(
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,
    val date: String,
    @Embedded
    val day: Day
)