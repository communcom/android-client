package io.golos.cyber_android.ui.shared

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.widgets.ImageButtonFixedIconSize
import kotlinx.android.synthetic.main.activity_image_viewer.*
import timber.log.Timber

/**
 * Activity that allows to see one image by its url
 */
class ImageViewerActivity : AppCompatActivity() {

    companion object {
        fun getIntent(context: Context, imageUrl: String) =
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
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setLightStatusBar()
        } else {
            clearLightStatusBar()
        }
        setLightStatusBar()
        findViewById<ImageButtonFixedIconSize>(R.id.backButton).setOnClickListener {
            finish()
        }
        findViewById<ImageButtonFixedIconSize>(R.id.shareButton).setOnClickListener { shareImage() }
        loadImage(intent.getStringExtra(Tags.ARGS))
    }

    private fun shareImage() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, intent.getStringExtra(Tags.ARGS))
            type = "text/plain"
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Timber.e("Share apps not found")
            Toast.makeText(this, getString(R.string.share_apps_not_found), Toast.LENGTH_LONG).show()
        }
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
                    shareButton.visibility = View.VISIBLE
                    progress.visibility = View.GONE
                    return false
                }
            })
            .into(image)
    }

    private fun setLightStatusBar() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else -> {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                )
            }
        }
    }

    private fun clearLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}