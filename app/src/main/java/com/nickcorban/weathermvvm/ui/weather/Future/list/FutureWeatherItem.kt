package com.nickcorban.weathermvvm.ui.weather.Future.list

import com.nickcorban.weathermvvm.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import data.database.unitlocalized.future.list.MetricSimpleFutureWeatherEntry
import data.database.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import internal.glide.GlideApp
import kotlinx.android.synthetic.main.item_future_weather.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureWeatherItem(
    val weatherEntry: UnitSpecificSimpleFutureWeatherEntry
): Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply{
            textView_condition.text = weatherEntry.conditionText
            updateDate()
            updateTemp()
            updateImg()

        }
    }

    override fun getLayout() = R.layout.item_future_weather

    private fun ViewHolder.updateDate() {
        val dtFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        textView_date.text = weatherEntry.date.format(dtFormatter)
    }

    private fun ViewHolder.updateTemp() {
        val unitAbbreviation = if (weatherEntry is MetricSimpleFutureWeatherEntry) "C"
        else "F"
        textView_temperature.text = "${weatherEntry.avgTemperature} $unitAbbreviation"
    }

    private fun ViewHolder.updateImg() {
        GlideApp.with(this.containerView)
            .load("http:" + weatherEntry.conditionIconUrl)
            .into(imageView_condition_icon)
    }

}