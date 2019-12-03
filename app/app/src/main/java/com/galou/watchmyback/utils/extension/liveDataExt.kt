package com.galou.watchmyback.utils.extension

import androidx.lifecycle.MutableLiveData

/**
 * @author galou
 * 2019-12-01
 */

/**
 * Force a [MutableLiveData] to emit its new value
 *
 * @param T
 */
fun <T> MutableLiveData<T>.emitNewValue() {
    this.value = value
}