package io.golos.cyber_android.ui.shared.widgets

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared.glide.loadCommentAttachment
import io.golos.cyber_android.ui.shared.glide.clear
import io.golos.cyber_android.ui.shared.utils.TextWatcherBase
import io.golos.domain.use_cases.post.post_dto.ContentBlock
import io.golos.domain.use_cases.post.post_dto.ImageBlock
import io.golos.domain.use_cases.post.post_dto.ParagraphBlock
import io.golos.domain.use_cases.post.post_dto.TextBlock
import kotlinx.android.synthetic.main.layout_comment_edit_block.view.*
import kotlinx.android.synthetic.main.layout_comment_image_attachment.view.*
import kotlinx.android.synthetic.main.layout_comment_input.view.*
import kotlinx.android.synthetic.main.layout_comment_input.view.root
import kotlinx.android.synthetic.main.widget_comment.view.*
import kotlin.properties.Delegates


class CommentWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var onSendClickListener: ((CommentContent) -> Unit)? = null

    var onClearClickListener: (() -> Unit)? = null

    var onAttachImageListener: ((String?) -> Unit)? = null

    private var contentId: ContentId? = null

    private var contentState: ContentState = ContentState.NEW

    private var attachmentImageUrl by Delegates.observable<String?>(null) { _, oldUrl, newUrl ->
        if (newUrl != oldUrl) {
            if (TextUtils.isEmpty(newUrl)) {
                commentAttachment.visibility = View.GONE
                sendButton.visibility = if (comment.text.isNullOrBlank()) View.GONE else View.VISIBLE
            } else {
                commentAttachment.visibility = View.VISIBLE
                sendButton.visibility = View.VISIBLE
                ivAttachment.loadCommentAttachment(newUrl, resources.getDimension(R.dimen.dimen_15).toInt())
            }
        }
    }

    init {
        inflate(context, R.layout.widget_comment, this)

        root.layoutTransition.setDuration(
            context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        )

        comment.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)

                sendButton.visibility = if (s.isNullOrBlank() && attachmentImageUrl == null) View.GONE else View.VISIBLE
            }
        })
        sendButton.setOnClickListener {
            onSendClickListener?.invoke(
                CommentContent(
                    contentId,
                    comment.text.toString(),
                    attachmentImageUrl?.let { Uri.parse(it) },
                    contentState)
            )
        }
        closeButton.setOnClickListener {
            clear()
            onClearClickListener?.invoke()
        }
        deleteAttachment.setOnClickListener {
            attachmentImageUrl = null
        }
        ivAttachImage.setOnClickListener {
            onAttachImageListener?.invoke(attachmentImageUrl)
        }
    }

    fun clear() {
        contentId = null
        attachmentImageUrl = null
        clearComment()
        clearEditState()
        clearAttachmentState()
        isEnabled = true
    }

    override fun setEnabled(enabled: Boolean) {
        comment.isEnabled = enabled
        sendButton.isEnabled = enabled
    }

    fun setCommentForEdit(id: ContentId?, body: ContentBlock?) {
        contentState = ContentState.EDIT
        contentId = id
        val content = body?.content ?: listOf()
        if (content.isNotEmpty()) {
            val block = content[0]
            if (block is ParagraphBlock) {
                val paragraphItemBlock = block.content.firstOrNull()
                if (paragraphItemBlock != null && paragraphItemBlock is TextBlock) {
                    val text = paragraphItemBlock.content
                    oldText.text = text
                    this.comment.setText(text)
                } else {
                    oldText.text = null
                }
            } else {
                oldText.text = null
            }
        }
        val mediaBlock = body?.attachments?.content?.firstOrNull()
        attachmentImageUrl = if (mediaBlock is ImageBlock) {
            mediaBlock.content.toString()
        } else {
            null
        }
        setupEditLayout(attachmentImageUrl)
        commentEdit.visibility = View.VISIBLE
    }

    fun setCommentForReply(id: ContentId?, body: ContentBlock?) {
        contentState = ContentState.REPLY
        contentId = id
        val content = body?.content ?: listOf()
        if (content.isNotEmpty()) {
            val block = content[0]
            if (block is ParagraphBlock) {
                val paragraphItemBlock = block.content.firstOrNull()
                if (paragraphItemBlock != null && paragraphItemBlock is TextBlock) {
                    val text = paragraphItemBlock.content
                    oldText.text = text
                } else {
                    oldText.text = null
                }
            } else {
                oldText.text = null
            }
        }
        val mediaBlock = body?.attachments?.content?.firstOrNull()
        val url = if (mediaBlock is ImageBlock) {
            mediaBlock.content.toString()
        } else {
            null
        }
        setupEditLayout(url)
        commentEdit.visibility = View.VISIBLE
    }

    private fun setupEditLayout(attachmentImageUrl: String?) {
        if (TextUtils.isEmpty(attachmentImageUrl)) {
            ivEditAttachment.visibility = View.GONE
        } else {
            ivEditAttachment.visibility = View.VISIBLE
            ivEditAttachment.loadCommentAttachment(attachmentImageUrl, resources.getDimension(R.dimen.dimen_4).toInt())
        }
        if(contentState == ContentState.EDIT){
            editCommentLabel.text = context.getString(R.string.edit_comment_send)
        } else{
            editCommentLabel.text = context.getString(R.string.edit_comment_reply)
        }
    }

    private fun clearEditState() {
        contentId = null
        contentState = ContentState.NEW
        commentEdit.visibility = View.GONE
        ivAttachment.clear()
        ivEditAttachment.clear()
    }

    private fun clearAttachmentState() {
        commentAttachment.visibility = View.GONE
        ivAttachment.clear()
    }

    private fun clearComment() {
        comment.setText("")
    }

    fun updateImageAttachment(photoFilePath: String?) {
        attachmentImageUrl = photoFilePath
    }

    data class CommentContent(
        val contentId: ContentId?,
        val message: String?,
        val imageUri: Uri?,
        val state: ContentState
    )

    enum class ContentState{
        NEW,
        EDIT,
        REPLY
    }
}