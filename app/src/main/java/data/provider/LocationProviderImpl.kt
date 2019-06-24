package data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import data.database.entity.WeatherLocation
import internal.LocationPermissionNotGranted
import internal.asDeferred
import kotlinx.coroutines.Deferred


const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class LocationProviderImpl(
    context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : PreferenceProvider(context), LocationProvider {

    private val appContext = context.applicationContext

    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try
        { hasDeviceLocationChanged(lastWeatherLocation)}
        catch (e:LocationPermissionNotGranted){
            false
        }



        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }

    override suspend fun getPreferredLocationString(): String {
        if (isUsingDeviceLocation()) {
            try{
                val deviceLocation = getLastDeviceLocation().await()
                    ?: return "${getCustomLocationName()}"
                return "${deviceLocation.latitude},${deviceLocation.longitude}"
                }
            catch (e : LocationPermissionNotGranted) {
                return "${getCustomLocationName()}"
            }
            }
        else
            return "${getCustomLocationName()}"
        }


    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean{
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocation().await()
            ?: return false

        val comparisonThreshold = 0.03
        return Math.abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                Math.abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean{
        if(!isUsingDeviceLocation()){
        val customLocationName = getCustomLocationName()
        return customLocationName != lastWeatherLocation.name
        }
        return false
    }

    private fun getCustomLocationName() : String? {
        return preferences.getString(CUSTOM_LOCATION,null)
    }

    private fun isUsingDeviceLocation(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION,true)
    }

    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocation(): Deferred<Location?> {

        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGranted()
    }
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

}