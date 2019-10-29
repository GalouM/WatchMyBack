package com.galou.watchmyback.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/integration/kotlinx-coroutines-play-services/src/Tasks.kt
 *
 * @param T
 * @return
 */

suspend fun <T> Task<T>.await(): Result<T> {
    // fast path
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                Result.Canceled(CancellationException("Task $this was cancelled normally."))
            } else {
                @Suppress("UNCHECKED_CAST")
                Result.Success(result as T)
            }
        } else {
            Result.Error(e)
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                @Suppress("UNCHECKED_CAST")
                if (isCanceled) cont.cancel() else  cont.resume(Result.Success(result as T))
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}

suspend fun <T> UploadTask.await(): Result<T> {
    // fast path
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                Result.Canceled(CancellationException("Task $this was cancelled normally."))
            } else {
                @Suppress("UNCHECKED_CAST")
                Result.Success(result as T)
            }
        } else {
            Result.Error(e)
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                @Suppress("UNCHECKED_CAST")
                if (isCanceled) cont.cancel() else cont.resume(Result.Success(result as T))
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Canceled(val exception: Exception?) : Result<Nothing>()
}


