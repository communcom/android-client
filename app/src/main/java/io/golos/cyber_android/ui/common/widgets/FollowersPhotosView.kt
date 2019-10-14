package io.golos.cyber_android.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_followers_photos.view.*

@Suppress("unused")
class FollowersPhotosView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val photoViews by lazy { arrayOf(photo0, photo1, photo2) }

    init {
        inflate(context, R.layout.view_followers_photos, this)
    }

    fun setPhotosUrls(urls: List<String>) {
        //clear all photos
        for (index in 0 until 3)
            Glide.with(this)
                .load(0)
                .into(photoViews[index])

        //load new ones
        for ((index, url) in urls.take(3).withIndex()) {
            Glide.with(this)
                .load(url)
                .load(R.drawable.img_example_avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(photoViews[index])
        }
    }
}