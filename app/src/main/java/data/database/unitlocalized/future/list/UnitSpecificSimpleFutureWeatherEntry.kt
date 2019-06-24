package data.database.unitlocalized.future.list

import org.threeten.bp.LocalDate

interface UnitSpecificSimpleFutureWeatherEntry {
        val date : LocalDate
        val avgTemperature : Double
        val conditionText : String
        val conditionIconUrl : String


}