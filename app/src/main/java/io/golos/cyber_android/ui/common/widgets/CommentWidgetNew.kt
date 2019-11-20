package io.golos.cyber_android.ui.common.widgets

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent
import io.golos.cyber_android.ui.common.extensions.loadAvatar
import io.golos.cyber_android.ui.utils.TextWatcherBase
import io.golos.domain.repositories.CurrentUserRepositoryRead
import kotlinx.android.synthetic.main.view_comment_widget.view.*
import javax.inject.Inject

class CommentWidgetNew
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var onSendClickListener: ((String) -> Unit)? = null

    @Inject
    internal lateinit var currentUserRepository: CurrentUserRepositoryRead

    init {
        App.injections.get<UIComponent>().inject(this)

        inflate(context, R.layout.view_comment_widget, this)

        root.layoutTransition.setDuration(context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())

        comment.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)

                sendButton.visibility = if(s.isNullOrBlank()) View.GONE else View.VISIBLE
            }
        })

        userAvatar.loadAvatar(currentUserRepository.userAvatarUrl)

        sendButton.setOnClickListener { onSendClickListener?.invoke(comment.text.toString()) }
    }

    fun setOnSendClickListener(listener: ((String) -> Unit)?) {
        onSendClickListener = listener
    }

    fun clearText() {
        comment.setText("")
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        comment.isEnabled = enabled
        sendButton.isEnabled = enabled
    }
}