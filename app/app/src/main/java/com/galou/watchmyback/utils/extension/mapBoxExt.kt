package com.galou.watchmyback.utils.extension

import android.content.Context
import android.graphics.BitmapFactory
import com.galou.watchmyback.R
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.utils.ICON_LOCATION_ACCENT
import com.galou.watchmyback.utils.ICON_LOCATION_PRIMARY
import com.galou.watchmyback.utils.ICON_LOCATION_PRIMARY_LIGHT
import com.galou.watchmyback.utils.createMapMarker
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager

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

fun Style.addIconLocationPrimaryLight(context: Context){
    addImage(ICON_LOCATION_PRIMARY_LIGHT, BitmapFactory.decodeResource(context.resources, R.drawable.icon_location_primary_light))
}

fun Style.addIconsLocation(context: Context){
    addIconLocationPrimary(context)
    addIconLocationAccent(context)
    addIconLocationPrimaryLight(context)
}


fun Map<String, Coordinate>.displayPointsOnMap(symbolManager: SymbolManager?, iconImage: String){
    for((id, coordinate) in this){
        symbolManager?.create(createMapMarker(coordinate, id, iconImage))
    }
}