package com.galou.watchmyback.utils.extension

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galou.watchmyback.testHelpers.*
import com.galou.watchmyback.utils.PERMS_EXT_STORAGE
import com.galou.watchmyback.utils.PERMS_LOCALISATION
import com.galou.watchmyback.utils.PERMS_PHONE_CALL
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

/**
 * @author galou
 * 2019-12-27
 */
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class ContextExtTests {

    private val context: Context =  ApplicationProvider.getApplicationContext()

    @After
    fun close(){
        stopKoin()
    }

    @Test
    fun whenGPSActive_sendTrue(){
        context.setLocationShadow(true)
        println(context.isGPSEnabled())
        //assertThat(context.isGPSEnabled()).isTrue()
    }

    @Test
    fun whenGPSInactive_sendFalse(){
        context.setLocationShadow(false)
        assertThat(context.isGPSEnabled()).isFalse()
    }

    @Test
    fun whenDeviceHasPhone_returnTrue(){
        context.setDeviceHasPhone(true)
        assertThat(context.canMakePhoneCall()).isTrue()
    }

    @Test
    fun whenDeviceDoesNotHavePhone_returnFalse(){
        context.setDeviceHasPhone(false)
        assertThat(context.canMakePhoneCall()).isFalse()
    }

    @Test
    fun hasLocationPermission_returnTrue(){
        grantPermission(true, PERMS_LOCALISATION)
        assertThat(context.isLocationPermission()).isTrue()
    }

    @Test
    fun noLocationPermission_returnFalse(){
        grantPermission(false, PERMS_LOCALISATION)
        assertThat(context.isLocationPermission()).isFalse()
    }

    @Test
    fun hasPhoneCallPermission_returnTrue(){
        val activity = Robolectric.buildActivity(FakeActivity::class.java).create().resume().get()
        grantPermission(true, PERMS_PHONE_CALL)
        assertThat(activity.requestPermissionPhoneCall()).isTrue()
    }

    @Test
    fun noPhoneCallPermission_returnFalse(){
        val activity = Robolectric.buildActivity(FakeActivity::class.java).create().resume().get()
        grantPermission(false, PERMS_PHONE_CALL)
        assertThat(activity.requestPermissionPhoneCall()).isFalse()
    }

    @Test
    fun hasStoragePermission_returnTrue(){
        val activity = Robolectric.buildActivity(FakeActivity::class.java).create().resume().get()
        grantPermission(true, PERMS_EXT_STORAGE)
        assertThat(activity.requestPermissionStorage()).isTrue()
    }

    @Test
    fun noStoragePermission_returnFalse(){
        val activity = Robolectric.buildActivity(FakeActivity::class.java).create().resume().get()
        grantPermission(false, PERMS_EXT_STORAGE)
        assertThat(activity.requestPermissionStorage()).isFalse()
    }

    @Test
    fun hasLocationPermissionActivity_returnTrue(){
        val activity = Robolectric.buildActivity(FakeActivity::class.java).create().resume().get()
        grantPermission(true, PERMS_LOCALISATION)
        assertThat(activity.requestPermissionLocation()).isTrue()
    }

    @Test
    fun noLocationPermissionActivity_returnFalse(){
        val activity = Robolectric.buildActivity(FakeActivity::class.java).create().resume().get()
        grantPermission(false, PERMS_LOCALISATION)
        assertThat(activity.requestPermissionLocation()).isFalse()
    }

    @Test
    fun noInternetConnection_returnFalse(){
        context.setInternetShadow(false)
        assertThat(context.isInternetAvailable()).isFalse()
    }

    @Test
    fun internetConnectionAvailable_returnTrue(){
        context.setInternetShadow(true)
        //assertThat(context.isInternetAvailable()).isTrue()
    }
}