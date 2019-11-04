package com.galou.watchmyback.utils

import android.app.Activity
import android.content.Intent
import com.galou.watchmyback.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.hasPermissions

/**
 * Created by galou on 2019-10-27
 */
fun intentSinglePicture(): Intent {
    return Intent().apply {
        action = Intent.ACTION_PICK
        type = IMAGE_ONLY_TYPE
    }
}

const val IMAGE_ONLY_TYPE = "image/*"

@AfterPermissionGranted(RC_IMAGE_PERMS)
fun requestPermissionStorage(activity: Activity): Boolean {
    if (!hasPermissions(activity, PERMS_EXT_STORAGE)) {
        EasyPermissions.requestPermissions(
            activity, activity.getString(R.string.app_storage_perm), RC_IMAGE_PERMS, PERMS_EXT_STORAGE)
        return(hasPermissions(activity, PERMS_EXT_STORAGE))
    }

    return true
}

