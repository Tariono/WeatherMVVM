package data.provider

import internal.UnitSystem

interface Provider {
    fun getUnitSystem() : UnitSystem
}