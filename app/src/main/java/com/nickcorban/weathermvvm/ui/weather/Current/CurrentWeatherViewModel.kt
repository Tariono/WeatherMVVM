package com.nickcorban.weathermvvm.ui.weather.Current

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider
import com.nickcorban.weathermvvm.ui.base.WeatherViewModel
import data.provider.Provider
import data.repository.ForecastRepository
import internal.UnitSystem
import internal.lazyDeferred

class CurrentWeatherViewModel (private val forecastRepository : ForecastRepository,
                                private val unitProvider : Provider
) : WeatherViewModel(forecastRepository,unitProvider)
{

    val weather by lazyDeferred{
        forecastRepository.getCurrentWeather(isMetric)
    }
}

