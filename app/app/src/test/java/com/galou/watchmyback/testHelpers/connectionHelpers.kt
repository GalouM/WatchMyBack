package com.galou.watchmyback.testHelpers

import android.content.Context
import android.location.LocationManager
import androidx.test.core.app.ApplicationProvider
import org.robolectric.Shadows

/**
 * @author galou
 * 2019-12-13
 */

fun setLocationShadow(enabled: Boolean) {
    val context: Context =  ApplicationProvider.getApplicationContext()
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val shadowOfLocationManager = Shadows.shadowOf(locationManager)
    shadowOfLocationManager.setLocationEnabled(enabled)

}