package com.galou.watchmyback.utils.extension

import android.content.Context
import android.os.Build
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galou.watchmyback.R
import com.galou.watchmyback.WatchMyBackApplication
import com.galou.watchmyback.data.entity.TimeDisplay
import com.galou.watchmyback.data.entity.UserPreferences
import com.galou.watchmyback.testHelpers.firstFriend
import com.galou.watchmyback.testHelpers.secondFriend
import com.google.android.material.textfield.TextInputEditText
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config
import java.util.*

/**
 * Created by galou on 2019-10-31
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class ViewExtUnitTest : KoinTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<WatchMyBackApplication>()
    }

    @After
    fun close(){
        stopKoin()
    }

    @Test
    @Throws(Exception::class)
    fun whenTrue_viewVisible(){
        val view = View(context)
        view.visibleOrInvisible(true)
        assertThat(view.visibility).isEqualTo(VISIBLE)

    }

    @Test
    @Throws(Exception::class)
    fun whenFalse_viewInvisible(){
        val view = View(context)
        view.visibleOrInvisible(false)
        assertThat(view.visibility).isEqualTo(INVISIBLE)
    }

    /*


    @Test
    fun inputLayout_showError(){
        val inputLayout = TextInputLayout(context)
        //inputLayout.errorMessage(null)

        //assertThat(inputLayout.error).isEqualTo(null)
        //assertThat(inputLayout.isErrorEnabled).isFalse()

        //inputLayout.errorMessage(0)
        //assertThat(inputLayout.error).isEqualTo(null)
        //assertThat(inputLayout.isErrorEnabled).isFalse()

        //inputLayout.errorMessage(R.string.test_message)
        //assertThat(inputLayout.error).isEqualTo(context.getString(R.string.test_message))
        //assertThat(inputLayout.isErrorEnabled).isTrue()


    }

     */

    @Test
    fun setEditTextWithExistingResource_showText(){
        val editText = TextInputEditText(context)
        editText.textFromResourceId(R.string.test_message)
        assertThat(editText.text.toString()).isEqualTo("Test Message")

        editText.textFromResourceId(90)
        assertThat(editText.text.toString()).isEmpty()
    }

    @Test
    fun setEditTextWithResource0_showEmptyText(){
        val editText = TextInputEditText(context)
        editText.textFromResourceId(0)
        assertThat(editText.text.toString()).isEmpty()
    }

    @Test
    fun setEditTextWithNull_showEmptyText(){
        val editText = TextInputEditText(context)
        editText.textFromResourceId(null)
        assertThat(editText.text.toString()).isEmpty()
    }

    @Test
    fun setEditTextWithNonExistingResource_showEmptyText(){
        val editText = TextInputEditText(context)
        editText.textFromResourceId(90)
        assertThat(editText.text.toString()).isEmpty()
    }

    @Test
    fun setEditTextWithDisplayNameUser_showUsername(){
        val editText = TextInputEditText(context)
        val users = listOf(firstFriend, secondFriend)
        editText.displayNameUsers(users)
        val textToDisplay = "${users[0].username} - ${users[1].username}"
        assertThat(editText.text.toString()).contains(textToDisplay)
    }

    @Test
    fun displayDateIntoEditText24h_rightFormat(){
        val editText = TextInputEditText(context)
        val userPreferences = UserPreferences(timeDisplay = TimeDisplay.H_24)
        val date = Calendar.getInstance()
        with(date){
            set(Calendar.YEAR, 1900)
            set(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_MONTH, 3)
            set(Calendar.HOUR_OF_DAY, 4)
            set(Calendar.MINUTE, 5)
        }
        editText.displayDate(userPreferences, date.time)
        assertThat(editText.text.toString()).isEqualTo("03/02/1900 - 04:05")
    }

    @Test
    fun displayDateIntoEditText12h_rightFormat(){
        val editText = TextInputEditText(context)
        val userPreferences = UserPreferences(timeDisplay = TimeDisplay.H_12)
        val date = Calendar.getInstance()
        with(date){
            set(Calendar.YEAR, 1900)
            set(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_MONTH, 3)
            set(Calendar.HOUR, 4)
            set(Calendar.MINUTE, 5)
        }
        editText.displayDate(userPreferences, date.time)
        assertThat(editText.text.toString()).isEqualTo("02/03/1900 - 04:05 a.m")
    }




}