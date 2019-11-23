package io.golos.cyber_android.ui.screens.profile_photos.view.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.PhotoPlace


/**
 * Photo view which contains avatar and cover photos
 */
class PostPhotoView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {

    private val drawableOverlay by lazy { context.resources.getDrawable(R.drawable.template_profile_photo_avatar, null) }

    private var photoPlace = PhotoPlace.AVATAR

    private val coverHeightFactor = 0.48

    private var onLoadingCompleteListener: ((Boolean) -> Unit)? = null

    init {
        scaleType = ScaleType.CENTER
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        if(photoPlace == PhotoPlace.AVATAR) {

            val bmp = drawableOverlay.toBitmap(width, height, Bitmap.Config.ARGB_8888)

            val src = Rect(0, 0, bmp.width, bmp.height)
            val dest = RectF(0f, 0f, width.toFloat(), height.toFloat())
            canvas?.drawBitmap(bmp, src, dest, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        when(photoPlace) {
            PhotoPlace.AVATAR -> setMeasuredDimension(width, width)
            PhotoPlace.COVER -> setMeasuredDimension(width, (width*coverHeightFactor).toInt())
        }
    }

    fun setMode(photoPlace: PhotoPlace) {
        if(this.photoPlace == photoPlace) {
            return
        }

        this.photoPlace = photoPlace

        when(photoPlace) {
            PhotoPlace.AVATAR -> {
                val params = layoutParams
                layoutParams.height = layoutParams.width
                layoutParams = params
            }

            PhotoPlace.COVER -> {
                val params = layoutParams
                layoutParams.height = (layoutParams.width * coverHeightFactor).toInt()
                layoutParams = params
            }
        }

        invalidate()
    }

    fun setOnLoadingCompleteListener(listener: ((Boolean) -> Unit)?) {
        onLoadingCompleteListener = listener
    }

    fun load(imageUrl: String?) {
        Glide.with(this).clear(this)

        Glide
            .with(this)
            .load(imageUrl)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    onLoadingCompleteListener?.invoke(false)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    onLoadingCompleteListener?.invoke(true)
                    return false
                }
            })
            .into(this)
    }
}