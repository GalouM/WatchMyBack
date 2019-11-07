package com.galou.watchmyback.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.galou.watchmyback.Event

/**
 * @author galou
 * 2019-11-05
 */
open class BaseViewModel : ViewModel() {

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    protected val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    /**
     * Emit a message
     *
     * @param message messgae to emit
     */
    protected fun showSnackBarMessage(message: Int){
        _snackbarText.value = Event(message)
    }
}