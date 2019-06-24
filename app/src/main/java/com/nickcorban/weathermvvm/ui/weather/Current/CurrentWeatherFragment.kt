package com.nickcorban.weathermvvm.ui.weather.Current

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
import data.network.ApixuWeatherAPIservice
import data.network.ConnectivityInterceptorImpl
import data.network.WeatherNetworkDataSource
import data.network.WeatherNetworkDataSourceImpl
import internal.glide.GlideApp
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CurrentWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()



    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(CurrentWeatherViewModel::class.java)

        bindUI()

    }

    private fun bindUI() = launch {
        val currentWeather = viewModel.weather.await()
        val weatherLocation = viewModel.weatherLocation.await()


        weatherLocation.observe(this@CurrentWeatherFragment, Observer{location ->
            if(location == null) return@Observer
            updateLocation(location.name)
        })


        currentWeather.observe(this@CurrentWeatherFragment, Observer{
            if(it == null) return@Observer


            group_loading.visibility = View.GONE
            updateLocation("Moscow")
            updateDaytoToday()
            updateTemperature(it.temperature, it.feelsLikeTemperature)
            updateCondition(it.conditionText)
            updatePrecipitation(it.precipitationVolume)
            updateWind(it.windDirection,it.windSpeed)
            updateVisibility(it.visibilityDistance)

            GlideApp.with(this@CurrentWeatherFragment)
                .load("http:${it.conditionIconUrl}")
                .into(imageView_condition_icon)
        })
    }

    private fun chooseLocal (metric: String, imperial : String): String{
        return if (viewModel.isMetric) metric else imperial
    }

    private fun updateLocation(location : String){
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }
    private fun updateDaytoToday(){
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Сегодня"
    }
    private fun updateTemperature(temperature : Double, feelsLike : Double){
        val unitsymbol = chooseLocal("C","F")
        textView_temperature.text = "$temperature $unitsymbol"
        textView_feels_like_temperature.text = "Ощущается как $feelsLike $unitsymbol"
    }
    private fun updateCondition(condition : String){
        textView_condition.text = condition
    }
    private fun updatePrecipitation(precipitationVolume : Double){
        val unitsymbol = chooseLocal("мм","дюйм(ов)")
        textView_precipitation.text = " Осадки : $precipitationVolume $unitsymbol"
    }
    private fun updateWind(windDirection: String,windSpeed: Double){
        val unitsymbol = chooseLocal("км/ч","ми/ч")
        textView_wind.text = "Ветер : $windDirection $windSpeed $unitsymbol"
    }
    private fun updateVisibility(visibilityDistance : Double){
        val unitsymbol = chooseLocal("км","ми")
        textView_visibility.text = "Видимость : $visibilityDistance $unitsymbol"

    }
}
