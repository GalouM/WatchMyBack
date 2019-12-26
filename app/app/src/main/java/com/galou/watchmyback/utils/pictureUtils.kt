package com.galou.watchmyback.utils

import android.content.Intent

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


