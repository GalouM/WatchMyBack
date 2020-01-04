package com.galou.watchmyback.testHelpers

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.test.core.app.ApplicationProvider
import com.galou.watchmyback.WatchMyBackApplication
import org.robolectric.Shadows

/**
 * @author galou
 * 2019-12-13
 */

fun Context.setLocationShadow(enabled: Boolean) {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val shadowOfLocationManager = Shadows.shadowOf(locationManager)
    shadowOfLocationManager.setLocationEnabled(enabled)

}

fun Context.setDeviceHasPhone(hasPhone: Boolean){
    val shadow = Shadows.shadowOf(packageManager)
    shadow.setSystemFeature(PackageManager.FEATURE_TELEPHONY, hasPhone)
}

fun grantPermission(granted: Boolean, permissionName: String){
    val application: WatchMyBackApplication = ApplicationProvider.getApplicationContext()
    val shadowApp = Shadows.shadowOf(application)
    if (granted){
        shadowApp.grantPermissions(permissionName)
    } else {
        shadowApp.denyPermissions(permissionName)
    }
}

fun Context.setInternetShadow(enabled: Boolean){
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val shadowConnectivityManager = Shadows.shadowOf(connectivityManager.activeNetworkInfo)
    when(enabled){
        true -> shadowConnectivityManager.setConnectionStatus(NetworkInfo.State.CONNECTED)
        false -> shadowConnectivityManager.setConnectionStatus(NetworkInfo.State.DISCONNECTED)
    }
}