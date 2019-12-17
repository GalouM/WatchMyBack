package com.galou.watchmyback.utils.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galou.watchmyback.R
import com.galou.watchmyback.WatchMyBackApplication
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config


/**
 * @author galou
 * 2019-11-24
 */

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class ImageViewUnitTest: KoinTest {

    private lateinit var context: Context
    private lateinit var imageView: ImageView
    private val resourceId = R.drawable.icon_bike
    private var drawableFromResource: Drawable.ConstantState? = null

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<WatchMyBackApplication>()
        imageView = ImageView(context)
        drawableFromResource = context.resources.getDrawable(resourceId, context.theme).constantState
    }

    @After
    fun close(){
        stopKoin()
    }

    @Test
    fun setImageFromResource_setImageCorrectly(){
        imageView.imageResource(resourceId)
        val viewDrawable = imageView.drawable.constantState
        assertThat(viewDrawable).isEqualTo(drawableFromResource)
    }

}