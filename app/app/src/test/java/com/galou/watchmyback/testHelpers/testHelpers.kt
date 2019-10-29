package com.galou.watchmyback.testHelpers

import androidx.lifecycle.LiveData
import com.galou.watchmyback.Event
import com.galou.watchmyback.data.entity.User
import org.junit.Assert.assertEquals

/**
 * Created by galou on 2019-10-24
 */
fun assertSnackBarMessage(snackbarLiveData: LiveData<Event<Int>>?, messageId: Int?){
    val  value: Event<Int>? = if (snackbarLiveData != null) LiveDataTestUtil.getValue(snackbarLiveData) else null
    assertEquals(value?.getContentIfNotHandled(), messageId)
}

const val TEST_UID = "uid"
const val TEST_EMAIL = "test@example.com"
const val TEST_NAME = "John Doe"
const val TEST_PHOTO_URI = "http://example.com/profile.png"
const val TEST_PHONE_NUMBER = "5554563454"

const val URI_STORAGE_REMOTE = "http://newUri"

const val NEW_USERNAME = "New Name"
const val NEW_PHONE_NB = "5553457897"
const val NEW_EMAIL = "new@email.com"

fun generateTestUser(id: String): User = User(
    id,
    TEST_EMAIL,
    TEST_NAME,
    TEST_PHONE_NUMBER,
    null
)