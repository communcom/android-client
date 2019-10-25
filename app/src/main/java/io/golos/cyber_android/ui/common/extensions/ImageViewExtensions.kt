package io.golos.cyber_android.ui.common.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R

fun ImageView.loadAvatar(avatarUrl: String?) {
    Glide
        .with(this)
        .load(avatarUrl)
        .apply(RequestOptions.circleCropTransform())
        .fallback(R.drawable.ic_empty_user)
        .error(R.drawable.ic_empty_user)
        .into(this)
}