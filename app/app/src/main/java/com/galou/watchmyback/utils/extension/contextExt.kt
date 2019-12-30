package com.galou.watchmyback.utils.extension

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import com.galou.watchmyback.R
import com.galou.watchmyback.utils.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author galou
 * 2019-12-26
 */

fun Context.isGPSEnabled(): Boolean = (applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)

fun Context.isLocationPermission(): Boolean = checkCallingOrSelfPermission(PERMS_LOCALISATION) == PackageManager.PERMISSION_GRANTED

fun Context.canMakePhoneCall(): Boolean = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

/**
 * Check if the application has Localisation permission and ask for it otherwise
 *
 * @return True if the application already has permission, false otherwise
 */
@AfterPermissionGranted(RC_LOCATION_PERMS)
fun Activity.requestPermissionLocation(): Boolean {
    if(! EasyPermissions.hasPermissions(this, PERMS_LOCALISATION)) {
        EasyPermissions.requestPermissions(
            this, getString(R.string.location_permission), RC_LOCATION_PERMS, PERMS_LOCALISATION
        )
        return false
    }
    return true
}

/**
 * Check if the application has Phone call permission and ask for it otherwise
 *
 * @return True if the application already has permission, false otherwise
 */
@AfterPermissionGranted(RC_PHONE_CALL_PERMS)
fun Activity.requestPermissionPhoneCall(): Boolean{
    if(! EasyPermissions.hasPermissions(this, PERMS_PHONE_CALL)) {
        EasyPermissions.requestPermissions(
            this, getString(R.string.phone_call_permission), RC_PHONE_CALL_PERMS, PERMS_PHONE_CALL
        )
        return false
    }
    return true
}

@AfterPermissionGranted(RC_IMAGE_PERMS)
fun Activity.requestPermissionStorage(): Boolean {
    if (!EasyPermissions.hasPermissions(this, PERMS_EXT_STORAGE)) {
        EasyPermissions.requestPermissions(
            this, getString(R.string.app_storage_perm), RC_IMAGE_PERMS, PERMS_EXT_STORAGE
        )
        return(EasyPermissions.hasPermissions(this, PERMS_EXT_STORAGE))
    }

    return true
}

fun Context.showNotification(message: String, title: String, channel: String, iconNotification: Int){
    val builder = NotificationCompat.Builder(this, channel)
        .setContentTitle(title)
        .setSmallIcon(iconNotification)
        .setContentText(message)
        .setPriority(PRIORITY_HIGH)
    with(NotificationManagerCompat.from(this)){
        notify(12, builder.build())
    }
}