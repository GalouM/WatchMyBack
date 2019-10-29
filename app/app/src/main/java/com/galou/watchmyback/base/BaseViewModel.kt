package com.galou.watchmyback.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Abstract Class used to build [ViewModel]
 *
 * Inherit from [ViewModel] and implement [CoroutineScope] in order to execute coroutines jobs
 *
 * @see ViewModel
 * @see CoroutineScope
 *
 */
abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private val compositeJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + compositeJob
}