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
import com.google.android.material.textfield.TextInputEditText
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config

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




}