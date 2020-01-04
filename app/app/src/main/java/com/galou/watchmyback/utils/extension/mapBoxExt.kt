package com.galou.watchmyback.utils.extension

import android.content.Context
import android.graphics.BitmapFactory
import com.galou.watchmyback.R
import com.galou.watchmyback.data.applicationUse.Coordinate
import com.galou.watchmyback.utils.ICON_LOCATION_ACCENT
import com.galou.watchmyback.utils.ICON_LOCATION_PRIMARY
import com.galou.watchmyback.utils.ICON_LOCATION_PRIMARY_LIGHT
import com.galou.watchmyback.utils.ICON_MAP_SIZE
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions

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

fun SymbolManager.createMapMarker(coordinate: Coordinate, pointId: String, iconImage: String, offset: Array<Float>){
    this.create(SymbolOptions()
        .withLatLng(LatLng(coordinate.latitude, coordinate.longitude))
        .withIconImage(iconImage)
        .withIconSize(ICON_MAP_SIZE)
        .withIconOffset(offset)
        .withTextField(pointId)
        .withTextOpacity(0f)
        .withIconAnchor("bottom")
    )

}

fun Map<String, Coordinate>.displayPointsOnMap(symbolManager: SymbolManager?, iconImage: String){
    // save how many points have the same coordinate to calculate the offset of each point
    val coordinateDuplication = mutableMapOf<Coordinate, Int>()
    for((id, coordinate) in this){
        coordinateDuplication.addCoordinate(coordinate)
        val offsetValue = coordinateDuplication.calculateOffset(coordinate)
        symbolManager?.createMapMarker(coordinate, id, iconImage, floatArrayOf(offsetValue, offsetValue).toTypedArray())
    }
}

fun MutableMap<Coordinate, Int>.addCoordinate(coordinate: Coordinate){
    if (coordinate in this){
        this[coordinate] = getValue(coordinate) + 1
    } else {
        this[coordinate] = 0
    }
}

fun MutableMap<Coordinate, Int>.calculateOffset(coordinate: Coordinate): Float{
    return getValue(coordinate) * 15f
}