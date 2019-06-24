package com.nickcorban.weathermvvm.ui.weather.Future.list

import androidx.lifecycle.ViewModel;
import com.nickcorban.weathermvvm.ui.base.WeatherViewModel
import data.provider.Provider
import data.repository.ForecastRepository
import internal.UnitSystem
import internal.lazyDeferred
import org.threeten.bp.LocalDate

class FutureListWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider : Provider
) : WeatherViewModel(forecastRepository,unitProvider) {


    val weatherEntries by lazyDeferred{
        forecastRepository.getFutureWeatherList(LocalDate.now(),isMetric)
    }
}
