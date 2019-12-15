package com.galou.watchmyback.testHelpers

import androidx.lifecycle.LiveData
import com.galou.watchmyback.Event
import com.galou.watchmyback.data.entity.*
import com.galou.watchmyback.utils.idGenerated
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

const val UUID_FIRST_FRIEND = "uuid_first_friend"
const val UUID_SECOND_FRIEND = "uuid_second_friend"

fun generateTestUser(id: String): User = User(
    id,
    TEST_EMAIL,
    TEST_NAME,
    TEST_PHONE_NUMBER,
    null,
    friendsId = mutableListOf(UUID_FIRST_FRIEND,UUID_SECOND_FRIEND)
)

val preferencesTest = UserPreferences(id = TEST_UID)

fun generateUserWithPref(id: String): UserWithPreferences{
    return UserWithPreferences(user = generateTestUser(id), preferences = preferencesTest)

}

val firstFriend = User(UUID_FIRST_FRIEND, "first-friend@gmail.com", "First Friend", "5550985674")
val secondFriend = User(UUID_SECOND_FRIEND, "second-friend@gmail.com", "Second Friend", "5555649834")

//check list
val checkList1 = CheckList(idGenerated, TEST_UID, TripType.BIKING, "List 1")
val checkList2 = CheckList(idGenerated, TEST_UID, TripType.HIKING, "List 2")

// items
val item1List1 = ItemCheckList(idGenerated, checkList1.id, "helmet", true)
val item2List1 = ItemCheckList(idGenerated, checkList1.id, "backpack", true)
val item3List1 = ItemCheckList(idGenerated, checkList1.id, "food", false)
val item1List2 = ItemCheckList(idGenerated, checkList2.id, "water bottle", false)
val item2List2 = ItemCheckList(idGenerated, checkList2.id, "backpack", true)

val itemList1 = listOf(
    item1List1,
    item2List1,
    item3List1
)
val itemList2 = listOf(
    item1List2,
    item2List2
)

val checkListWithItem1 = CheckListWithItems(checkList = checkList1, items = itemList1)
val checkListWithItems2 = CheckListWithItems(checkList = checkList2, items = itemList2)

val checkLists = listOf(checkListWithItem1, checkListWithItems2)

val tripWithData = TripWithData(
    trip = Trip(),
    points = mutableListOf(),
    watchers = mutableListOf()
)