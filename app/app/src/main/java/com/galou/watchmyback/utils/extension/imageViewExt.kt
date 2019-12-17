package com.galou.watchmyback.utils.extension

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.galou.watchmyback.R
import com.galou.watchmyback.utils.GlideApp

/**
 * Load an URL  into a [ImageView] using [Glide] and crop it in circle
 *
 * If the url doesn't contain any image it will load a default image
 *
 * @param url image url
 *
 * @see Glide
 * @see BindingAdapter
 * @see RequestOptions.circleCropTransform
 */
@BindingAdapter("loadUrlInCircle")
fun ImageView.bindLoadUrlInCircle(url: String?) {
    val glide = GlideApp.with(this.context)
    glide
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .error(glide.load(R.drawable.icon_my_profile))
        .into(this)


}

/**
 * Load an URL  into a [ImageView] using [Glide]
 *
 * If the url doesn't contain any image it will load a default image
 *
 * @param url image url
 *
 * @see Glide
 * @see BindingAdapter
 */
@BindingAdapter("loadUrl")
fun ImageView.bindLoadUrl(url: String?){
    val glide = GlideApp.with(this.context)
    glide
        .load(url)
        .apply(RequestOptions.centerCropTransform())
        .error(glide.load(R.drawable.icon_friends))
        .into(this)
}

/**
 * Load a drawable from its resource id to an ImageView
 *
 * @see BindingAdapter
 * @see ImageView.setImageResource
 *
 * @param imageResource
 */
@BindingAdapter("imageResouce")
fun ImageView.imageResource(imageResource: Int?){
    if(imageResource != null && imageResource != 0)
        setImageResource(imageResource)

}