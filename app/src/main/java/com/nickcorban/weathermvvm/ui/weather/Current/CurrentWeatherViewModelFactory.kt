package com.nickcorban.weathermvvm.ui.weather.Current

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import data.provider.Provider
import data.repository.ForecastRepository

class CurrentWeatherViewModelFactory(
    private val forecastRepository: ForecastRepository,
    private val unitProvider: Provider
) : ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrentWeatherViewModel(forecastRepository, unitProvider) as T
    }
}