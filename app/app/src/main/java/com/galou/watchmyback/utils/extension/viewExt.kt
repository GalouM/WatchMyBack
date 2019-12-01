package com.galou.watchmyback.utils.extension

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.galou.watchmyback.Event
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by galou on 2019-10-25
 */

/**
 * Show [Snackbar] from a [View]
 *
 * @param snackbarText SnackBar message
 * @param timeLength How long the Snackbar should be displayed
 *
 * @see Snackbar.config
 */
fun View.showSnackBar(snackbarText: String, timeLength: Int){
    Snackbar.make(this, snackbarText, timeLength).apply {
        config(view.context)
        view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
        show()
    }
}

/**
 * Setup a [Observer] for a Snackbar
 *
 * @param lifecycleOwner
 * @param snackbarEvent [LiveData] to listen to
 * @param timeLength How long the Snackbar should be displayed
 *
 * @see Event
 * @see LiveData
 * @see View.showSnackBar
 */
fun View.setupSnackBar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
){
    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let { showSnackBar(context.getString(it), timeLength) }
    })
}

/**
 * Binding Adapter define the visibility of a [View]
 *
 * @param visibility true is the [View] is visible, false otherwise
 *
 * @see BindingAdapter
 */
@BindingAdapter("visibleOrInvisible")
fun View.visibleOrInvisible(visibility: Boolean){
    when(visibility){
        true -> this.visibility = View.VISIBLE
        false -> this.visibility = View.INVISIBLE
    }
}

/**
 * Binding Adapter allow to show an error on a [TextInputLayout]
 *
 * @param errorMessage ID of the String to display
 *
 * @see BindingAdapter
 */
@BindingAdapter("errorMessage")
fun TextInputLayout.errorMessage(errorMessage: Int?){
    if(errorMessage != null && errorMessage != 0){
        val resources = this.resources
        this.error = (resources.getString(errorMessage))
    } else {
        this.error = null
        this.isErrorEnabled = false
    }
}

/**
 * Set text of an [TextInputEditText] from a resource ID
 *
 * @param resourceId: ID of the string
 *
 * @see BindingAdapter
 * @see TextInputEditText.setText
 */
@BindingAdapter("textFromResourceId")
fun TextInputEditText.textFromResourceId(resourceId: Int?){
    if (resourceId != 0 && resourceId != null){
        try {
            setText(resourceId)
        } catch (e: Exception){
            setText("")
        }
    } else {
        setText("")
    }
}

/**
 * Set the start Icon of an [TextInputLayout] from the Id od a drawable
 *
 * @param resourceId ID of the drawable
 *
 * @see BindingAdapter
 * @see TextInputLayout.setStartIconDrawable
 */
@BindingAdapter("iconFromResourceId")
fun TextInputLayout.iconFromResourceId(resourceId: Int?){
    if(resourceId != 0) resourceId?.let{
        try {
            startIconDrawable = ContextCompat.getDrawable(context, resourceId)
        } catch (e: Exception) { }
    }
}


