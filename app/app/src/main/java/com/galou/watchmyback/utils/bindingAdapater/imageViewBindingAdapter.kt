package com.galou.watchmyback.utils.bindingAdapater

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.galou.watchmyback.R

/**
 * Created by galou on 2019-10-25
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

@BindingAdapter("loadUrl")
fun ImageView.bindLoadUrl(url: String?){
    Glide.with(this.context).load(url).apply(RequestOptions.centerCropTransform()).into(this)
}