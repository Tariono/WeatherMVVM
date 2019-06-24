package com.nickcorban.weathermvvm.ui.weather.Future.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import data.provider.Provider
import data.repository.ForecastRepository
import org.threeten.bp.LocalDate


class FutureDetailWeatherViewModelFactory(
    private val detailDate: LocalDate,
    private val forecastRepository: ForecastRepository,
    private val unitProvider : Provider
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FutureDetailWeatherViewModel(detailDate,forecastRepository,unitProvider) as T
    }
}