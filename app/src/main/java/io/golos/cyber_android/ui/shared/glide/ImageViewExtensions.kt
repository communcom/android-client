package io.golos.cyber_android.ui.shared.glide

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
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
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.shared.glide.transformations.GradientTransformation
import io.golos.cyber_android.ui.shared.glide.transformations.PercentageRoundVectorFrameTransformation
import io.golos.cyber_android.ui.shared.glide.transformations.RoundFrameTransformation
import io.golos.cyber_android.ui.shared.glide.transformations.TopRoundedCornersTransformation
import io.golos.domain.GlobalConstants
import kotlinx.android.synthetic.main.view_post_embed_website.view.*

typealias GlideTarget = Target<*>

enum class ImageProgressLoadState{
    START,
    COMPLETE,
    ERROR
}

fun ImageView.loadAvatar(avatarUrlString: String?, @DrawableRes defaultRes: Int = R.drawable.ic_avatar) =
    this.load(avatarUrlString?.let { Uri.parse(it) }, defaultRes)

fun ImageView.loadAvatar(avatarUri: Uri?, @DrawableRes defaultRes: Int = R.drawable.ic_avatar) =
    this.load(avatarUri, defaultRes)

fun ImageView.loadCommunity(communityUrl: String?) = this.load(communityUrl?.let { Uri.parse(it) }, R.drawable.ic_commun)

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
        .fallback(R.drawable.ic_avatar)
        .error(R.drawable.ic_avatar)
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

fun ImageView.load(url: String?, @DrawableRes defaultRes: Int): GlideTarget =
    this.load(url?.let { Uri.parse(it) }, defaultRes)

fun ImageView.load(uri: Uri?, @DrawableRes defaultRes: Int): GlideTarget =
    Glide
        .with(this)
        .load(uri)
        .apply(RequestOptions.circleCropTransform())
        .fallback(defaultRes)
        .error(defaultRes)
        .placeholder(defaultRes)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)

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

fun ImageView.loadWebsiteContent(url: String?){
    val radius = context.resources.getDimension(R.dimen.radius_corner_embed_website)
    Glide
        .with(this)
        .load(url)
        .transform(CenterCrop(),
            TopRoundedCornersTransformation(radius)
        )
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .error(R.drawable.widget_epmty_place_holder)
        .placeholder(R.drawable.widget_epmty_place_holder)
        .into(image)
}

fun ImageView.loadVideoContent(url: String?){
    Glide
        .with(this)
        .load(url)
        .centerCrop()
        .error(R.drawable.widget_epmty_place_holder)
        .placeholder(R.drawable.widget_epmty_place_holder)
        .into(image)
}

fun ImageView.loadCommunityItemCover(url: String?): GlideTarget =
    Glide
        .with(this.context.applicationContext)
        .load(if (url.isNullOrEmpty()) "file:///android_asset/bcg_blue.webp" else url)
        .transform(
            CenterCrop(),
            RoundedCorners(this.context.resources.getDimension(R.dimen.profile_communities_list_item_bcg_corner).toInt())
        )
        .placeholder(R.drawable.bcg_community_item_loading_background)
        .into(this)

fun ImageView.loadCommunityItemAvatar(url: String?): GlideTarget =
    Glide
        .with(this.context.applicationContext)
        .load(if (url.isNullOrEmpty()) "file:///android_asset/bcg_blue.webp" else url)
        .transform(
            CircleCrop(),
            RoundFrameTransformation(
                this.context.applicationContext,
                R.dimen.stroke_thin,
                when(App.getInstance().keyValueStorage.getUIMode()){
                    GlobalConstants.UI_MODE_LIGHT -> R.color.white
                    GlobalConstants.UI_MODE_DARK -> R.color.white_dark_theme
                    else -> {
                        if(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
                            R.color.white_dark_theme
                        }else{
                            R.color.white
                        }
                    }
                }
            )
        )
        .override(100, 100)
        .into(this)

fun ImageView.loadNotificationImageContent(url: String?){
    val roundSize = context.resources.getDimension(R.dimen.notification_content_image_round_size).toInt()
    Glide.with(context)
        .load(url.orEmpty())
        .transform(CenterCrop(), RoundedCorners(roundSize))
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}

fun ImageView.clear(){
    val viewContext = context
    if(viewContext is Activity && viewContext.isDestroyed){
        return
    }
    Glide.with(this).clear(this)
}

fun GlideTarget.clear(context: Context) = Glide.with(context).clear(this)