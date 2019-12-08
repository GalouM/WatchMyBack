package com.galou.watchmyback.utils.rvAdapter

import com.galou.watchmyback.data.entity.TripUpdateFrequency

/**
 * @author galou
 * 2019-12-03
 */
interface UpdateFrequencyListener {
    fun onClickFrequency(frequency: TripUpdateFrequency)
}