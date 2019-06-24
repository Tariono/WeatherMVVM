package com.nickcorban.weathermvvm

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import com.nickcorban.weathermvvm.ui.weather.Current.CurrentWeatherViewModelFactory
import com.nickcorban.weathermvvm.ui.weather.Future.detail.FutureDetailWeatherViewModel
import com.nickcorban.weathermvvm.ui.weather.Future.detail.FutureDetailWeatherViewModelFactory
import com.nickcorban.weathermvvm.ui.weather.Future.list.FutureWeatherListViewModelFactory
import data.database.ForecastDatabase
import data.network.*
import data.provider.LocationProvider
import data.provider.LocationProviderImpl
import data.provider.Provider
import data.provider.ProviderImpl
import data.repository.ForecastRepository
import data.repository.ForecastRepositoryImpl
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import org.threeten.bp.LocalDate

class ForecastApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@ForecastApplication))

        bind() from singleton {ForecastDatabase(instance())}
        bind() from singleton {instance<ForecastDatabase>().currentWeatherDao()}
        bind() from singleton {instance<ForecastDatabase>().futureWeatherDao()}
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance())}
        bind() from singleton {ApixuWeatherAPIservice(instance())}
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance())}
        bind() from provider{ LocationServices.getFusedLocationProviderClient(instance<Context>())}
        bind<LocationProvider>() with singleton {LocationProviderImpl(instance(),instance())}
        bind() from singleton {instance<ForecastDatabase>().currentLocationDao()}
        bind<ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(), instance(), instance(),instance(),instance()) }
        bind<Provider>() with singleton{ ProviderImpl(instance()) }
        bind() from provider { CurrentWeatherViewModelFactory(instance(), instance())}
        bind() from provider { FutureWeatherListViewModelFactory(instance(),instance())}
        bind() from factory { detailDate:LocalDate -> FutureDetailWeatherViewModelFactory(detailDate, instance(), instance()) }


    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}