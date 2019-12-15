package com.galou.watchmyback.utils.extension

import android.content.Context
import android.graphics.BitmapFactory
import com.galou.watchmyback.R
import com.galou.watchmyback.utils.ICON_LOCATION_ACCENT
import com.galou.watchmyback.utils.ICON_LOCATION_PRIMARY
import com.mapbox.mapboxsdk.maps.Style

/**
 * @author galou
 * 2019-12-12
 */

fun Style.addIconLocationPrimary(context: Context){
    addImage(ICON_LOCATION_PRIMARY, BitmapFactory.decodeResource(context.resources, R.drawable.icon_location_primary))
}

fun Style.addIconLocationAccent(context: Context){
    addImage(ICON_LOCATION_ACCENT, BitmapFactory.decodeResource(context.resources, R.drawable.icon_location_accent))
}