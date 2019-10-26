package io.golos.cyber_android.ui.shared_fragments.post.view.binding_adapters

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.common.extensions.parentActivity
import io.golos.cyber_android.ui.common.widgets.CommentWidgetEdit
import io.golos.cyber_android.ui.common.widgets.CommentWidgetNew

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

@BindingAdapter("text")
fun setCommentEditText(view: CommentWidgetEdit, valueToBind: LiveData<List<CharSequence>>?) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer { text ->
                view.setText(text)
            })
        }
    }
}