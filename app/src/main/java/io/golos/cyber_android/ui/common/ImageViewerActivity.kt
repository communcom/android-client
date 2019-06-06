package io.golos.cyber_android.ui.common

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.Tags
import kotlinx.android.synthetic.main.activity_image_viewer.*

/**
 * Activity that allows to see one image by its url
 */
class ImageViewerActivity : AppCompatActivity() {

    companion object {
        fun getIntent(
            context: Context,
            imageUrl: String
        ) =
            Intent(context, ImageViewerActivity::class.java).apply {
                putExtra(
                    Tags.ARGS,
                    imageUrl
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        loadImage(intent.getStringExtra(Tags.ARGS))
    }

    private fun loadImage(url: String?) {
        progress.visibility = View.VISIBLE
        Glide.with(this)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress.visibility = View.GONE
                    return false
                }
            })
            .into(image)
    }
}
