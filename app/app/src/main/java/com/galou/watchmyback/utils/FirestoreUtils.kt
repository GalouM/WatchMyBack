package com.galou.watchmyback.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Await for completion of an [Task] without blocking a thread
 *
 * Return a [Result] with data or and error
 *
 * This suspending function is cancellable.
 * If the Job of the current coroutine is cancelled or completed while this suspending function is waiting, this function
 * stops waiting for the completion stage and immediately resumes with [CancellationException].
 *
 * Took and modified from "https://github.com/Kotlin/kotlinx.coroutines"
 *
 * @param T Type of [Result] to return
 * @return a [Result] with its data if the task succeed or its error
 *
 * @see Task
 * @see Result
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

/**
 * Await for completion of an [UploadTask] without blocking a thread
 *
 * Return a [Result] with data or and error
 *
 * This suspending function is cancellable.
 * If the Job of the current coroutine is cancelled or completed while this suspending function is waiting, this function
 * stops waiting for the completion stage and immediately resumes with [CancellationException].
 *
 * Took and modified from "https://github.com/Kotlin/kotlinx.coroutines"
 *
 * @param T Type of [Result] to return
 * @return a [Result] with its data if the task succeed or its error
 *
 * @see UploadTask
 * @see Result
 */
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

/**
 * Generic class that holds a value and its status
 *
 * @param R Type of result
 */
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Canceled(val exception: Exception?) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Canceled -> "Canceled[exception=$exception]"
        }
    }
}


