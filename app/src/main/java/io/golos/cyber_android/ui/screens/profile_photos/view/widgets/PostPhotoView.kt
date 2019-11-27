package io.golos.cyber_android.ui.screens.profile_photos.view.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoViewImageInfo

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

    private var photoPlace = ProfileItem.AVATAR

    private val coverHeightFactor = 0.48

    private var onLoadingCompleteListener: ((Boolean) -> Unit)? = null

    init {
        scaleType = ScaleType.CENTER
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        if(photoPlace == ProfileItem.AVATAR) {

            val bmp = drawableOverlay.toBitmap(width, height, Bitmap.Config.ARGB_8888)

            val src = Rect(0, 0, bmp.width, bmp.height)
            val dest = RectF(0f, 0f, width.toFloat(), height.toFloat())
            canvas?.drawBitmap(bmp, src, dest, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)

        when(photoPlace) {
            ProfileItem.AVATAR -> setMeasuredDimension(width, width)
            ProfileItem.COVER -> setMeasuredDimension(width, (width*coverHeightFactor).toInt())
        }
    }

    fun setMode(profileItem: ProfileItem) {
        if(this.photoPlace == profileItem) {
            return
        }

        this.photoPlace = profileItem

        when(profileItem) {
            ProfileItem.AVATAR -> {
                val params = layoutParams
                layoutParams.height = layoutParams.width
                layoutParams = params
            }

            ProfileItem.COVER -> {
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

    fun load(imageUrl: String?, isImageFromCamera: Boolean) {
        Glide.with(this).clear(this)

        Glide
            .with(this)
            .load(imageUrl)
            .apply {
                if(isImageFromCamera && photoPlace == ProfileItem.COVER) {
                    this.transform(CenterCrop())
                }
            }
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

    fun getImageInfo(): PhotoViewImageInfo {
        val drawableRect = Rect(0, 0, 0, 0)
        getDrawingRect(drawableRect)

        return PhotoViewImageInfo(
            (drawable as BitmapDrawable).bitmap,
            imageMatrix,
            drawableRect
        )
    }
}