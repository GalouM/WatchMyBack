package com.galou.watchmyback.testHelpers

import android.app.Activity
import androidx.core.net.toUri
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.utils.idGenerated
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.util.concurrent.Executor

/**
 * Created by galou on 2019-10-25
 */


data class MockTask<T>(private val resultObject: T?, private val success: Boolean) : Task<T>(){
    override fun isComplete(): Boolean = true

    override fun getException(): Exception? = Exception("Something went wrong")

    override fun addOnFailureListener(p0: OnFailureListener): Task<T> = this

    override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<T> = this

    override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<T> = this

    override fun addOnCompleteListener(p0: Activity, p1: OnCompleteListener<T>): Task<T> = this

    override fun addOnCompleteListener(p0: OnCompleteListener<T>): Task<T> = this

    override fun addOnCompleteListener(p0: Executor, p1: OnCompleteListener<T>): Task<T> = this

    override fun getResult(): T? = resultObject

    override fun <X : Throwable?> getResult(p0: Class<X>): T? = resultObject

    override fun addOnSuccessListener(p0: OnSuccessListener<in T>): Task<T> = this

    override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in T>): Task<T> = this

    override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in T>): Task<T> = this

    override fun isSuccessful(): Boolean = success

    override fun isCanceled(): Boolean = false

}

fun generateTestUser(id: String) = User(
    id, TEST_EMAIL, TEST_NAME, TEST_PHONE_NUMBER, TEST_PHOTO_URI)