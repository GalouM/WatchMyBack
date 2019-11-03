package com.galou.watchmyback

import androidx.lifecycle.Observer

/**
 * Wrapper for data that is exposed via a LiveData that represent an event
 *
 * @param T Type of LiveData emitted
 * @property content content emitted
 *
 *
 * @author Galou Minisini
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Make sure the [Event] has not been handled before
     *
     * @return content or null if it has been handled
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled){
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}

/**
 * [Observer] for an [Event]
 *
 * Check if the [Event]'s content has already been handled
 *
 * @param T type of content
 * @property onEventUnhandledContent only called if the [Event]'s contents has not been handled.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit): Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { onEventUnhandledContent(it) }
    }
}