package com.galou.watchmyback.utils

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import com.galou.watchmyback.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author galou
 * 2019-12-08
 */


/**
 * Check if the application has Localisation permission and ask for it otherwise
 *
 * @param activity
 * @return True if the application already has permission, false otherwise
 */
@AfterPermissionGranted(RC_LOCATION_PERMS)
fun requestPermissionLocation(activity: Activity): Boolean{
    if(! EasyPermissions.hasPermissions(activity, PERMS_LOCALISATION)) {
        EasyPermissions.requestPermissions(
            activity, activity.getString(R.string.location_permission), RC_LOCATION_PERMS, PERMS_LOCALISATION)
        return false
    }
    return true
}

/**
 * Check if the GPS is activated
 *
 * @param context activity context
 * @return True is the GPS is available, false otherwise
 */
fun isGPSAvailable(context: Context): Boolean{
    val service = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return service.isProviderEnabled(LocationManager.GPS_PROVIDER)
}