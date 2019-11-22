package com.galou.watchmyback.utils.extension

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.CheckList
import com.galou.watchmyback.data.entity.TripType
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

@BindingAdapter("typeCheckListImage")
fun ImageView.typeCheckListImage(type: TripType){
    val drawable = when(type){
        TripType.BIKING -> R.drawable.icon_bike
        TripType.MOUNTAIN_BIKING -> R.drawable.icon_mtb
        TripType.HIKING -> R.drawable.icon_hike
        TripType.RUNNING -> R.drawable.icon_run
        TripType.SKIING -> R.drawable.icon_ski
        TripType.MOTORCYCLE -> R.drawable.icon_motorcycle
    }

    GlideApp.with(context).load(drawable).into(this)

}