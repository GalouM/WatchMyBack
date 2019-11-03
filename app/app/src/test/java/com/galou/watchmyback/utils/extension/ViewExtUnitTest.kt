package com.galou.watchmyback.utils.extension

import android.content.Context
import android.os.Build
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.test.core.app.ApplicationProvider
import com.galou.watchmyback.R
import com.galou.watchmyback.WatchMyBackApplication
import com.galou.watchmyback.utils.extension.errorMessage
import com.galou.watchmyback.utils.extension.visibleOrInvisible
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by galou on 2019-10-31
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
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
    fun whenTrue_viewVisible(){
        val view = View(context)
        view.visibleOrInvisible(true)
        assertThat(view.visibility).isEqualTo(VISIBLE)

    }

    @Test
    fun whenFalse_viewInsisible(){
        val view = View(context)
        view.visibleOrInvisible(false)
        assertThat(view.visibility).isEqualTo(INVISIBLE)
    }

    /*

    @Test
    fun inputLayout_showError(){
        val inputLayout = TextInputLayout(context)
        inputLayout.errorMessage(null)

        //assertThat(inputLayout.error).isEqualTo(null)
        //assertThat(inputLayout.isErrorEnabled).isFalse()

        inputLayout.errorMessage(0)
        //assertThat(inputLayout.error).isEqualTo(null)
        //assertThat(inputLayout.isErrorEnabled).isFalse()

        inputLayout.errorMessage(R.string.test_message)
        assertThat(inputLayout.error).isEqualTo(context.getString(R.string.test_message))
        assertThat(inputLayout.isErrorEnabled).isTrue()


    }

     */
}