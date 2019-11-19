package com.galou.watchmyback.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by galou on 2019-10-20
 */
class OtherUtilUnitTest {

    @Test
    fun checkIdGenerated_isUnique(){
        val firstId = idGenerated
        val secondId = idGenerated
        assertThat(firstId).doesNotMatch(secondId)
    }

    @Test
    fun todayDate_isToday(){
        val today = Calendar.getInstance(Locale.CANADA).time
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.CANADA)
        assertThat(formatter.format(today)).isEqualTo(formatter.format(todaysDate))
    }

    @Test
    fun failLocalRequestAndFailRemoteRequest_returnFail(){
        val remote = Result.Error(Exception("test error"))
        val local = Result.Error(Exception("test error"))

        assertThat(returnSuccessOrError(local, remote)).isEqualTo(local)
    }

    @Test
    fun cancelLocalRequestAndCancelRemoteRequest_returnCanceled(){
        val remote = Result.Canceled(Exception("test error"))
        val local = Result.Canceled(Exception("test error"))

        assertThat(returnSuccessOrError(local, remote)).isEqualTo(local)
    }

    @Test
    fun failLocalRequestAndSuccessRemoteRequest_returnRemote(){
        val remote = Result.Success(null)
        val local = Result.Error(Exception("test error"))

        assertThat(returnSuccessOrError(local, remote)).isEqualTo(local)
    }

    @Test
    fun canceledLocalRequestAndSuccessRemoteRequest_returnLocal(){
        val remote = Result.Success(null)
        val local = Result.Canceled(Exception("test error"))

        assertThat(returnSuccessOrError(local, remote)).isEqualTo(local)
    }

    @Test
    fun successLocalRequestAndFailRemoteRequest_returnRemote(){
        val remote = Result.Error(Exception("test error"))
        val local = Result.Success(null)

        assertThat(returnSuccessOrError(local, remote)).isEqualTo(remote)
    }

    @Test
    fun successLocalRequestAndCanceledRemoteRequest_returnRemote(){
        val remote = Result.Canceled(Exception("test error"))
        val local = Result.Success(null)

        assertThat(returnSuccessOrError(local, remote)).isEqualTo(remote)
    }

    @Test
    fun localAndRemoteSuccess_returnSuccess(){
        val local = Result.Success(null)
        val remote = Result.Success(null)
        assertThat(returnSuccessOrError(local, remote)).isEqualTo(Result.Success(null))

    }
}