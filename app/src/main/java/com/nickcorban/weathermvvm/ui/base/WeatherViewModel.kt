package com.nickcorban.weathermvvm.ui.base

import androidx.lifecycle.ViewModel
import data.provider.Provider
import data.repository.ForecastRepository
import internal.UnitSystem
import internal.lazyDeferred

abstract class WeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider : Provider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()

    val isMetric : Boolean
        get() = unitSystem == UnitSystem.METRIC


    val weatherLocation by lazyDeferred{
        forecastRepository.getWeatherLocation()
    }
}