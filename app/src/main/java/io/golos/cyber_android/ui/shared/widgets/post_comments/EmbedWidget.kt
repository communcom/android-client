package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.glide.ImageProgressLoadState
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.glide.loadContentAttachment
import io.golos.cyber_android.ui.shared.utils.prefetchScreenSize
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.EmbedBlock
import kotlinx.android.synthetic.main.layout_image_preload.view.*
import kotlinx.android.synthetic.main.view_attachment_rich.view.*
import kotlinx.android.synthetic.main.view_attachment_rich.view.flPreloadImage

class EmbedWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    BlockWidget<EmbedBlock, EmbedWidgetListener> {

    private var onClickProcessor: EmbedWidgetListener? = null

    private var linkUri: Uri? = null

    private var contentId: ContentIdDomain? = null

    private var cornerRadius: Int = 0

    private var widthBlock: Int = 0

    private @ColorRes var preloadFrameColorId: Int = R.color.post_empty_place_holder

    init {
        inflate(context, R.layout.view_attachment_rich, this)
    }

    fun setContentId(postContentId: ContentIdDomain) {
        contentId = postContentId
    }

    fun setCornerRadius(cornerRadius: Int){
        this.cornerRadius = cornerRadius
    }

    fun setWidthBlock(widthBlock: Int){
        this.widthBlock = widthBlock
    }

    fun setPreloadFrameColor(@ColorRes colorId: Int){
        preloadFrameColorId = colorId
    }

    override fun setOnClickProcessor(processor: EmbedWidgetListener?) {
        this.onClickProcessor = processor
    }

    override fun render(block: EmbedBlock) {
        linkUri = block.url
        val thumbnailUrl = block.thumbnailUrl?.prefetchScreenSize(context)
        var currentThumbnail: ImageView? = null
        flPreloadImage.setBackgroundColor(ContextCompat.getColor(context, preloadFrameColorId))
        if (thumbnailUrl != null) {
            val thumbnailHeight = block.thumbnailHeight
            val thumbnailWidth = block.thumbnailWidth
            currentThumbnail = if(thumbnailHeight == null || thumbnailWidth == null){
                richImage.visibility = View.GONE
                richImageAspectRatio.visibility = View.VISIBLE
                richImageAspectRatio
            } else{
                richImageAspectRatio.visibility = View.GONE
                richImage.visibility = View.VISIBLE
                val layoutParams = richImage.layoutParams as ConstraintLayout.LayoutParams
                if(widthBlock == 0){
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                } else{
                    val heightBlock = widthBlock.toFloat() / thumbnailWidth * thumbnailHeight
                    layoutParams.height = heightBlock.toInt()
                }
                richImage.layoutParams = layoutParams
                richImage
            }
            richDescription.visibility = View.GONE

            btnRetry.setOnClickListener {
                loadImage(currentThumbnail, thumbnailUrl.toString())
            }
            loadImage(currentThumbnail, thumbnailUrl.toString())
        } else {
            richImage.visibility = View.GONE
            richImageAspectRatio.visibility = View.GONE
            richDescription.visibility = View.VISIBLE
            richDescription.text = block.description
        }

        val type = Type.getTypeByProviderName(block.providerName.orEmpty())
        richIcon.setImageResource(type.providerIconResId)
        richName.text = block.author
        richUrl.text = block.authorUrl?.authority

        if(onClickProcessor != null) {
            currentThumbnail?.setOnClickListener {
                if (contentId != null) {
                    onClickProcessor?.onItemClicked(contentId!!)
                } else {
                    thumbnailUrl?.let { uri ->
                        onClickProcessor?.onImageClicked(uri)
                    }
                }
            }
            llLinkProvider.setOnClickListener {
                linkUri?.let {
                    this.onClickProcessor?.onLinkClicked(it)
                }
            }
        } else {
            llLinkProvider.setOnClickListener(null)
            currentThumbnail?.setOnClickListener(null)
        }
    }

    private fun loadImage(imageView: ImageView, url: String?){
        imageView.loadContentAttachment(url, {
            when(it){
                ImageProgressLoadState.START -> {
                    pbImageLoad.visibility = View.VISIBLE
                    flPreloadImage.visibility = View.VISIBLE
                    btnRetry.visibility = View.INVISIBLE
                }
                ImageProgressLoadState.COMPLETE -> {
                    pbImageLoad.visibility = View.INVISIBLE
                    btnRetry.visibility = View.INVISIBLE
                    flPreloadImage.visibility = View.GONE
                }
                ImageProgressLoadState.ERROR -> {
                    pbImageLoad.visibility = View.INVISIBLE
                    btnRetry.visibility = View.VISIBLE
                }
            }
        }, cornerRadius)
    }

    override fun release() {
        richImage.clear()
        richImageAspectRatio.clear()
    }

    enum class Type(
        val providerName: String,
        @DrawableRes val providerIconResId: Int
    ) {
        OTHER("", R.drawable.ic_rich_other);

        companion object {
            fun getTypeByProviderName(providerName: String): Type {
                return values().find { type -> type.providerName == providerName } ?: OTHER
            }
        }
    }
}