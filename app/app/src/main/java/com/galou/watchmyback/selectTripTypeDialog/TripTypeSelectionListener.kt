package com.galou.watchmyback.selectTripTypeDialog

import com.galou.watchmyback.data.entity.TripType

/**
 * @author galou
 * 2019-11-24
 */
interface TripTypeSelectionListener {

    fun onClickType(type: TripType)
}