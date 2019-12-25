package com.galou.watchmyback.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.galou.watchmyback.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

/**
 * Utils class use to generate data
 *
 * @author Galou Minisini
 */

/**
 * Generate a unique and random [String] using the class [UUID]
 */
var idGenerated: String = ""
    get() {
        field = UUID.randomUUID().toString()
        return field
    }

/**
 * Generate todays date as [Date] using the class [Calendar]
 */
var todaysDate: Date = Calendar.getInstance(TimeZone.getDefault()).time

fun displayData(message: String) = Log.e("WMT DATA", message)

fun returnSuccessOrError(localResult: Result<Void?>, remoteResult: Result<Void?>): Result<Void?>{
    return when {
        localResult is Result.Success && remoteResult is Result.Success -> {
            Result.Success(null)
        }
        localResult is Result.Error|| localResult is Result.Canceled -> localResult
        remoteResult is Result.Error|| remoteResult is Result.Canceled -> remoteResult
        else -> remoteResult
    }
}

/**
 * Check if the application has Phone call permission and ask for it otherwise
 *
 * @param activity
 * @return True if the application already has permission, false otherwise
 */
@AfterPermissionGranted(RC_PHONE_CALL_PERMS)
fun requestPermissionPhoneCall(activity: Activity): Boolean{
    if(! EasyPermissions.hasPermissions(activity, PERMS_PHONE_CALL)) {
        EasyPermissions.requestPermissions(
            activity, activity.getString(R.string.phone_call_permission), RC_PHONE_CALL_PERMS, PERMS_PHONE_CALL)
        return false
    }
    return true
}


/**
 * Check if the device can make phone call or not
 *
 * @param context context of the activity
 * @return
 */
fun deviceCanMakePhoneCall(context: Context): Boolean{
    val packageManager = context.packageManager
    return packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
}


