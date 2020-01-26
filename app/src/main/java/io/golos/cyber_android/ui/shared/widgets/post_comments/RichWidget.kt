package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared.glide.ImageProgressLoadState
import io.golos.cyber_android.ui.shared.glide.loadContentAttachment
import io.golos.cyber_android.ui.shared.glide.release
import io.golos.cyber_android.ui.shared.utils.prefetchScreenSize
import io.golos.domain.use_cases.post.post_dto.RichBlock
import kotlinx.android.synthetic.main.layout_image_preload.view.*
import kotlinx.android.synthetic.main.view_attachment_rich.view.*
import kotlinx.android.synthetic.main.view_attachment_rich.view.flPreloadImage

class RichWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    BlockWidget<RichBlock, RichWidgetListener> {

    private var onClickProcessor: RichWidgetListener? = null

    private var linkUri: Uri? = null

    private var contentId: ContentId? = null

    private var cornerRadius: Int = 0

    private var widthBlock: Int = 0

    init {
        inflate(context, R.layout.view_attachment_rich, this)
    }

    fun setContentId(postContentId: ContentId) {
        contentId = postContentId
    }

    fun setCornerRadius(cornerRadius: Int){
        this.cornerRadius = cornerRadius
    }

    fun setWidthBlock(widthBlock: Int){
        this.widthBlock = widthBlock
    }

    override fun setOnClickProcessor(processor: RichWidgetListener?) {
        this.onClickProcessor = processor
    }

    override fun render(block: RichBlock) {
        linkUri = block.url
        val thumbnailUrl = block.thumbnailUrl?.prefetchScreenSize(context)
        var currentThumbnail: ImageView? = null
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

        val type = EmbedWidget.Type.getTypeByProviderName(block.providerName.orEmpty())
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
        richImage.release()
        richImageAspectRatio.release()
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