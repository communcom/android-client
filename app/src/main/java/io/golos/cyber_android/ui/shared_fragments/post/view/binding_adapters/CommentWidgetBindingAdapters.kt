package io.golos.cyber_android.ui.shared_fragments.post.view.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.common.extensions.parentActivity
import io.golos.cyber_android.ui.common.widgets.CommentWidgetEdit
import io.golos.cyber_android.ui.common.widgets.CommentWidgetNew
import io.golos.cyber_android.ui.shared_fragments.post.dto.EditReplyCommentSettings

/**
 * One-way binding adapter for comment field
 */
@BindingAdapter("enabled")
fun setCommentEnableState(view: CommentWidgetNew, valueToBind: LiveData<Boolean>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer { isEnabled ->
                view.isEnabled = isEnabled
            })
        }
    }
}

@BindingAdapter("enabled")
fun setCommentEditEnableState(view: CommentWidgetEdit, valueToBind: LiveData<Boolean>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer { isEnabled ->
                view.isEnabled = isEnabled
            })
        }
    }
}

@BindingAdapter("settings")
fun setCommentEditText(view: CommentWidgetEdit, valueToBind: LiveData<EditReplyCommentSettings>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer { texts ->
                view.updateSettings(texts)
            })
        }
    }
}