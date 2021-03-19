package com.npsappprojects.darkweather

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat

class LocationHelper {

    private val LOCATION_REFRESH_TIME = 1000 // 3 seconds. The Minimum Time to get location update
    private val LOCATION_REFRESH_DISTANCE = 300 // 30 meters. The Minimum Distance to be changed to get location update
    private val MY_PERMISSIONS_REQUEST_LOCATION = 100

    var myLocationListener: MyLocationListener? = null

    interface MyLocationListener {
        fun onLocationChanged(location: Location)
    }


    fun startListeningUserLocation(context: Context, myListener: MyLocationListener) {
        myLocationListener = myListener

        val mLocationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

        val mLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                //your code here
                myLocationListener!!.onLocationChanged(location) // calling listener to inform that updated location is available
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
// check for permissions
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (mLocationManager.isLocationEnabled) {
                    mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        LOCATION_REFRESH_TIME.toLong(),
                        LOCATION_REFRESH_DISTANCE.toFloat(),
                        mLocationListener
                    )

                } else {
                    mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        LOCATION_REFRESH_TIME.toLong(),
                        LOCATION_REFRESH_DISTANCE.toFloat(),
                        mLocationListener
                    )

                }
            } else {
                mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME.toLong(),
                    LOCATION_REFRESH_DISTANCE.toFloat(),
                    mLocationListener
                )

            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (mLocationManager.isLocationEnabled) {
                    mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        LOCATION_REFRESH_TIME.toLong(),
                        LOCATION_REFRESH_DISTANCE.toFloat(),
                        mLocationListener
                    )

                } else {
                    mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        LOCATION_REFRESH_TIME.toLong(),
                        LOCATION_REFRESH_DISTANCE.toFloat(),
                        mLocationListener
                    )

                }
            } else {
                mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_REFRESH_TIME.toLong(),
                    LOCATION_REFRESH_DISTANCE.toFloat(),
                    mLocationListener
                )

            }
        }
    }
}