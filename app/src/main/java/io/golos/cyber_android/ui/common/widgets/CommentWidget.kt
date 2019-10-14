package io.golos.cyber_android.ui.common.widgets

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.utils.PostConstants
import io.golos.cyber_android.ui.common.utils.*
import kotlinx.android.synthetic.main.view_comment_widget.view.*


class CommentWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface Listener {
        fun onSendClick(text: CharSequence)
        fun onGalleryClick()
        fun onUserNameCleared()
        fun onCommentChanged(text: CharSequence)
    }

    var listener: Listener? = null

    var isSendEnabled: Boolean = false
        set(value) {
            field = value
        }

    init {
        inflate(context, R.layout.view_comment_widget, this)

        comment.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                s?.let {
                    listener?.onCommentChanged(it)
                }
                s?.colorizeHashTags()
                s?.colorizeLinks()
            }
        })

        comment.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                listener?.onSendClick(comment.text ?: "")
            }
            false
        }

//        comment.movementMethod = MultilineLinkMovementMethod()
        comment.filters = arrayOf(InputFilter.LengthFilter(PostConstants.MAX_COMMENT_CONTENT_LENGTH))
    }

    /**
     * Clears comment text. Typically needs to be called when comment was sent successfully
     */
    fun clearText() {
        userName = null
        comment.setText("")
        comment.filters = arrayOf(InputFilter.LengthFilter(PostConstants.MAX_COMMENT_CONTENT_LENGTH))
    }

    private var userName: String? = null

    fun setUserToReply(userName: String?) {
        val prevUserName = this.userName
        this.userName = userName
        //if there is previous username then remove it
        if (prevUserName != null && comment.text?.startsWith(prevUserName) == true)
            comment.setText(comment.text?.replaceRange(0, "$prevUserName ".length, ""))

        if (userName != null) {
            if (comment.text?.startsWith(userName) == false)
                addUserName(userName)
            comment.filters = arrayOf(InputFilter { source, _, _, dest, _, _ ->
                if (dest.length > source.length) {
                    if (dest.toString().compareTo(userName) == 0) {
                        listener?.onUserNameCleared()
                        return@InputFilter ""
                    }
                }
                return@InputFilter source
            }, InputFilter.LengthFilter(PostConstants.MAX_COMMENT_CONTENT_LENGTH + userName.length))

            ViewUtils.showKeyboard(comment)
        }
    }

    private fun addUserName(userName: String) {
        val newText = SpannableStringBuilder("$userName ${comment.text}")
        newText.setSpan(StyleSpan(Typeface.BOLD), 0, userName.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        comment.text = newText
        comment.setSelection(newText.length)
    }
}