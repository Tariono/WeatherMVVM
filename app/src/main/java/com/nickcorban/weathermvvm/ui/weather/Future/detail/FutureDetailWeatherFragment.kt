package com.nickcorban.weathermvvm.ui.weather.Future.detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import com.nickcorban.weathermvvm.R
import com.nickcorban.weathermvvm.ui.base.ScopedFragment
import data.database.LocalDateConverter
import internal.DateNotFoundException
import internal.glide.GlideApp
import kotlinx.android.synthetic.main.future_detail_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureDetailWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModelFactoryInstanceFactory :  ((LocalDate) ->
        FutureDetailWeatherViewModelFactory) by factory()

    private lateinit var viewModel: FutureDetailWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.future_detail_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val safeArgs = arguments?.let { FutureDetailWeatherFragmentArgs.fromBundle(it) }
        val date = LocalDateConverter.stringToDate(safeArgs?.dateString) ?:  throw DateNotFoundException()

        viewModel = ViewModelProviders.of(this, viewModelFactoryInstanceFactory(date))
            .get(FutureDetailWeatherViewModel::class.java)
        bindUI()
    }



    private fun bindUI() = launch(Dispatchers.Main) {

        val detailedWeather = viewModel.weatherDetail.await()

        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(this@FutureDetailWeatherFragment, Observer { location ->
            if (location == null) return@Observer
            updateLocation(location.name)
        })

        detailedWeather.observe(this@FutureDetailWeatherFragment, Observer {weatherEntry ->
            if (weatherEntry == null) return@Observer

            updateDate(weatherEntry.date)
            updateTemperatures(weatherEntry.avgTemperature, weatherEntry.minTemperature,
                weatherEntry.maxTemperature)
            updateCondition(weatherEntry.conditionText)
            updatePrecipitation(weatherEntry.totalPrecipitation)
            updateWind(weatherEntry.maxWindSpeed)
            updateVisibility(weatherEntry.avgVisibilityDistance)
            updateUv(weatherEntry.uv)

            GlideApp.with(this@FutureDetailWeatherFragment)
                .load("http:${weatherEntry.conditionIconUrl}")
                .into(imageView_condition_icon)
        })
    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetric) metric else imperial
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDate(date: LocalDate) {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
                date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    }

    private fun updateTemperatures(temperature: Double, min : Double, max : Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("°C", "°F")
        textView_temperature.text = "$temperature$unitAbbreviation"
        textView_min_max_temperature.text = "От $min до $max $unitAbbreviation"
    }

    private fun updateCondition(condition: String) {
        textView_condition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("мм", "дй")
        textView_precipitation.text = "Осадки: $precipitationVolume $unitAbbreviation"
    }

    private fun updateWind( windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("кмч", "мч")
        textView_wind.text = "Ветер: $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("км", "миль")
        textView_visibility.text = "Видимость: $visibilityDistance $unitAbbreviation"

    }
    private fun updateUv (uv : Double){
        textView_uv.text = "УФ: $uv"

}

}
