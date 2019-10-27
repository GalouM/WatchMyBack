package com.galou.watchmyback.testHelpers

import androidx.lifecycle.LiveData
import com.galou.watchmyback.Event
import org.junit.Assert.assertEquals

/**
 * Created by galou on 2019-10-24
 */
fun assertSnackBarMessage(snackbarLiveData: LiveData<Event<Int>>, messageId: Int){
    val  value: Event<Int> =
        LiveDataTestUtil.getValue(snackbarLiveData)
    assertEquals(value.getContentIfNotHandled(), messageId)
}