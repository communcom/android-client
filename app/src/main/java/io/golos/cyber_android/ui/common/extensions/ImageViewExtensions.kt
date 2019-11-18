package io.golos.cyber_android.ui.common.extensions

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.glide.PercentageRoundFrameTransform

fun ImageView.loadAvatar(avatarUrl: String?) = this.load(avatarUrl, R.drawable.ic_empty_user)

fun ImageView.loadCommunity(communityUrl: String?) = this.load(communityUrl, R.drawable.ic_group_temporary)

fun ImageView.loadLeader(url: String?, percentage: Float) =
    Glide
        .with(this)
        .load(if(url.isNullOrEmpty()) "file:///android_asset/empty_user.webp" else url)
        .transform(
            CircleCrop(),
            PercentageRoundFrameTransform(
                this.context.applicationContext,
                0.8f,
                percentage,
                R.color.avatar_frame,
                R.drawable.ic_avatar_frame
            )
        )
        .fallback(R.drawable.ic_empty_user)
        .error(R.drawable.ic_empty_user)
        .into(this)

fun ImageView.load(url: String?, @DrawableRes defaultRes: Int) {
    Glide
        .with(this)
        .load(if(url.isNullOrEmpty()) "file:///android_asset/empty_user.webp" else url)
        .apply(RequestOptions.circleCropTransform())
        .fallback(defaultRes)
        .error(defaultRes)
        .into(this)
}