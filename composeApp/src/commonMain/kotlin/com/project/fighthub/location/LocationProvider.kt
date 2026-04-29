package com.project.fighthub.location

sealed class LocationStatus {
    data class Available(val lat: Double, val lng: Double) : LocationStatus()
    object PermissionDenied : LocationStatus()
    object Disabled : LocationStatus()
    data class Error(val message: String? = null) : LocationStatus()
}

fun interface LocationProvider {
    fun requestLocation(onResult: (LocationStatus) -> Unit)
}

object StubLocationProvider : LocationProvider {
    override fun requestLocation(onResult: (LocationStatus) -> Unit) {
        onResult(LocationStatus.Disabled)
    }
}

