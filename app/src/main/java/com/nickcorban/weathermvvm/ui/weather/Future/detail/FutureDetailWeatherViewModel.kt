package com.nickcorban.weathermvvm.ui.weather.Future.detail

import androidx.lifecycle.ViewModel;
import com.nickcorban.weathermvvm.ui.base.WeatherViewModel
import data.provider.Provider
import data.repository.ForecastRepository
import internal.lazyDeferred
import org.threeten.bp.LocalDate

class FutureDetailWeatherViewModel(
    private val detailDate: LocalDate,
    private val forecastRepository: ForecastRepository,
    unitProvider : Provider
) : WeatherViewModel(forecastRepository, unitProvider) {


    val weatherDetail by lazyDeferred {
        forecastRepository.getDetailedFutureWeather(detailDate,isMetric)
    }
}
