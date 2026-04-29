package com.project.fighthub.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class AndroidLocationProvider(
    private val activity: ComponentActivity,
    private val requestPermission: () -> Unit
) : LocationProvider {

    private val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var pendingCallback: ((LocationStatus) -> Unit)? = null

    override fun requestLocation(onResult: (LocationStatus) -> Unit) {
        if (!hasLocationPermission()) {
            pendingCallback = onResult
            requestPermission()
            return
        }

        if (!isLocationEnabled()) {
            onResult(LocationStatus.Disabled)
            return
        }

        val lastKnown = getLastKnownLocation()
        if (lastKnown != null) {
            onResult(LocationStatus.Available(lastKnown.latitude, lastKnown.longitude))
            return
        }

        requestSingleUpdate(onResult)
    }

    fun onPermissionResult(granted: Boolean) {
        val callback = pendingCallback
        pendingCallback = null
        if (callback == null) return
        if (!granted) {
            callback(LocationStatus.PermissionDenied)
            return
        }
        requestLocation(callback)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gpsEnabled || networkEnabled
    }

    private fun getLastKnownLocation(): Location? {
        val gps = runCatching { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) }.getOrNull()
        val network = runCatching { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) }.getOrNull()
        return listOfNotNull(gps, network).maxByOrNull { it.time }
    }

    private fun requestSingleUpdate(onResult: (LocationStatus) -> Unit) {
        val criteria = Criteria().apply {
            accuracy = Criteria.ACCURACY_FINE
        }
        val provider = locationManager.getBestProvider(criteria, true)
        if (provider == null) {
            onResult(LocationStatus.Error("No location provider available"))
            return
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                onResult(LocationStatus.Available(location.latitude, location.longitude))
                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
            override fun onProviderEnabled(provider: String) = Unit
            override fun onProviderDisabled(provider: String) = Unit
        }

        locationManager.requestSingleUpdate(provider, listener, null)
    }
}

