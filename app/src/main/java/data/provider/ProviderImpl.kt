package data.provider

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import internal.UnitSystem

class ProviderImpl(context: Context) : PreferenceProvider(context), Provider {

    override fun getUnitSystem(): UnitSystem {
        val selectedName = preferences.getString("UNIT_SYSTEM",UnitSystem.METRIC.name)
        return UnitSystem.valueOf(selectedName!!)
    }
}