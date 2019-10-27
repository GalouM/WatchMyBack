package com.galou.watchmyback.utils.extension

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.galou.watchmyback.R

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
fun ImageView.bindLoadUrlInCircle(url: String?){
    val glide = Glide.with(this.context)
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
    Glide.with(this.context).load(url).apply(RequestOptions.centerCropTransform()).into(this)
}