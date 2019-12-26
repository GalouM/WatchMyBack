package com.galou.watchmyback.utils

import com.galou.watchmyback.data.applicationUse.Coordinate
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions

/**
 * @author galou
 * 2019-12-25
 */

fun createMapMarker(coordinate: Coordinate, pointId: String, iconImage: String): SymbolOptions = SymbolOptions()
    .withLatLng(LatLng(coordinate.latitude, coordinate.longitude))
    .withIconImage(iconImage)
    .withIconSize(ICON_MAP_SIZE)
    .withIconOffset(ICON_MAP_OFFSET)
    .withTextField(pointId)
    .withTextOpacity(0f)