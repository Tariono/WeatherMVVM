package data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import data.network.response.CurrentWeatherResponse
import data.network.response.FutureWeatherResponse
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



const val API_KEY = ""

//Интерфейс для получения информации с API
interface ApixuWeatherAPIservice {


    //Функция для получения информации по текущему дню, на выходе отдает ожидание ответа с эндпоинта
    @GET("current.json")
    fun getCurrentWeather(
        @Query("q") location : String,
        @Query("lang") languageCode : String = "en"
    ) : Deferred<CurrentWeatherResponse>

    //Функция для получения информации по следующим семи дням, на выходе отдает ожидание ответа с эндпоинта
    @GET("forecast.json")
    fun getFutureWeather(
        @Query("q") location: String,
        @Query("days") days : Int,
        @Query("lang") languageCode: String = "en"
    ) : Deferred<FutureWeatherResponse>

    companion object {
        operator fun invoke(connectivityInterceptor: ConnectivityInterceptor)
                : ApixuWeatherAPIservice {
            //встраиваем перехватчик, который добавит ключ в запрос
            val requestInterceptor = Interceptor{ chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)

            }
                //создаем клиент OkHttp
            val okHttpClient = OkHttpClient.Builder()
                //встраиваем перехватчик с ключем
                .addInterceptor(requestInterceptor)
                //встраиваем перехватчик с проверкой соединения с сетью
                .addInterceptor(connectivityInterceptor)
                .build()

            //возвращаем построенный запрос
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://api.apixu.com/v1/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApixuWeatherAPIservice::class.java)
        }
    }
}