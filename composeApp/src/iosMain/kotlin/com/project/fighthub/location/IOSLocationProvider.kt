package com.project.fighthub.location

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSError
import platform.darwin.NSObject

class IOSLocationProvider : NSObject(), LocationProvider, CLLocationManagerDelegateProtocol {
    private val manager = CLLocationManager()
    private var pendingCallback: ((LocationStatus) -> Unit)? = null

    init {
        manager.delegate = this
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun requestLocation(onResult: (LocationStatus) -> Unit) {
        if (!CLLocationManager.locationServicesEnabled()) {
            onResult(LocationStatus.Disabled)
            return
        }

        when (authorizationStatus()) {
            kCLAuthorizationStatusNotDetermined -> {
                pendingCallback = onResult
                manager.requestWhenInUseAuthorization()
            }
            kCLAuthorizationStatusDenied, kCLAuthorizationStatusRestricted -> {
                onResult(LocationStatus.PermissionDenied)
            }
            kCLAuthorizationStatusAuthorizedAlways, kCLAuthorizationStatusAuthorizedWhenInUse -> {
                pendingCallback = onResult
                manager.requestLocation()
            }
            else -> onResult(LocationStatus.Error("Unknown authorization status"))
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun authorizationStatus(): CLAuthorizationStatus {
        return CLLocationManager.authorizationStatus()
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val callback = pendingCallback
        pendingCallback = null
        val location = didUpdateLocations.lastOrNull() as? CLLocation
        if (callback == null) return
        if (location == null) {
            callback(LocationStatus.Error("No location found"))
            return
        }
        callback(LocationStatus.Available(location.coordinate.latitude, location.coordinate.longitude))
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        val callback = pendingCallback
        pendingCallback = null
        callback?.invoke(LocationStatus.Error(didFailWithError.localizedDescription))
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val callback = pendingCallback ?: return
        when (authorizationStatus()) {
            kCLAuthorizationStatusAuthorizedAlways, kCLAuthorizationStatusAuthorizedWhenInUse -> manager.requestLocation()
            kCLAuthorizationStatusDenied, kCLAuthorizationStatusRestricted -> {
                pendingCallback = null
                callback(LocationStatus.PermissionDenied)
            }
            else -> Unit
        }
    }
}

