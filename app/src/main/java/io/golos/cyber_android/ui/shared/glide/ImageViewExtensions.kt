package io.golos.cyber_android.ui.shared.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.glide.transformations.GradientTransformation
import io.golos.cyber_android.ui.shared.glide.transformations.PercentageRoundVectorFrameTransformation
import io.golos.cyber_android.ui.shared.glide.transformations.RoundFrameTransformation

enum class ImageProgressLoadState{
    START,
    COMPLETE,
    ERROR
}

fun ImageView.loadAvatar(avatarUrl: String?) = this.load(avatarUrl, R.drawable.ic_empty_user)

fun ImageView.loadCommunity(communityUrl: String?) = this.load(communityUrl, R.drawable.ic_group_temporary)

fun ImageView.loadLeader(url: String?, percentage: Float) =
    Glide
        .with(this)
        .load(if (url.isNullOrEmpty()) "file:///android_asset/empty_user.webp" else url)
        .transform(
            CircleCrop(),
            PercentageRoundVectorFrameTransformation(
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

fun ImageView.loadCover(url: String?) {
    val urlToLoad = if (url.isNullOrEmpty()) "file:///android_asset/bcg_blue.webp" else url

    Glide
        .with(this)
        .load(urlToLoad)
        .transform(
            CenterCrop(),
            GradientTransformation(
                this.context.applicationContext,
                R.color.cover_gradient_start_color,
                R.color.cover_gradient_end_color
            )
        )
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}

fun ImageView.load(url: String?, @DrawableRes defaultRes: Int) {
    Glide
        .with(this)
        .load(url)
        .apply(RequestOptions.circleCropTransform())
        .fallback(defaultRes)
        .error(defaultRes)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}

fun ImageView.loadCommentAttachment(url: String?, cornerRadiusInPixels: Int = 0) {
    Glide.with(context)
        .load(url.orEmpty())
        .transform(CenterCrop(), RoundedCorners(cornerRadiusInPixels))
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}

fun ImageView.loadContentAttachment(url: String?, loadStatus: ((ImageProgressLoadState) -> Unit), cornerRadius: Int = 0){
    loadStatus.invoke(ImageProgressLoadState.START)
    val requestBuilder = Glide
        .with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    if(cornerRadius != 0){
        requestBuilder.transform(RoundedCorners(cornerRadius))
    }
    requestBuilder
        .listener(object: RequestListener<Drawable>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                loadStatus.invoke(ImageProgressLoadState.ERROR)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                loadStatus.invoke(ImageProgressLoadState.COMPLETE)
                return false
            }

        })
        .into(this)
}

fun ImageView.loadCommunityItemCover(url: String?): Target<*> =
    Glide
        .with(this.context.applicationContext)
        .load(if (url.isNullOrEmpty()) "file:///android_asset/bcg_blue.webp" else url)
        .transform(
            CenterCrop(),
            RoundedCorners(this.context.resources.getDimension(R.dimen.profile_communities_list_item_bcg_corner).toInt())
        )
        .placeholder(R.drawable.bcg_community_item_loading_background)
        .into(this)

fun ImageView.loadCommunityItemAvatar(url: String?): Target<*> =
    Glide
        .with(this.context.applicationContext)
        .load(if (url.isNullOrEmpty()) "file:///android_asset/bcg_blue.webp" else url)
        .transform(
            CircleCrop(),
            RoundFrameTransformation(
                this.context.applicationContext,
                R.dimen.stroke_thin,
                R.color.white
            )
        )
        .override(100, 100)
        .into(this)


fun ImageView.clear() = Glide.with(this).clear(this)

fun Target<*>.clear(context: Context) = Glide.with(context).clear(this)