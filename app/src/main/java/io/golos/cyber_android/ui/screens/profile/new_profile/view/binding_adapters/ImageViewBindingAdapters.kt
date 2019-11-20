package io.golos.cyber_android.ui.screens.profile.new_profile.view.binding_adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.extensions.getDrawableRes
import io.golos.cyber_android.ui.common.extensions.loadAvatar
import io.golos.cyber_android.ui.common.extensions.loadCover
import io.golos.cyber_android.ui.common.extensions.parentActivity

@BindingAdapter("avatar")
fun setAvatarBinding(view: ImageView, valueToBind: LiveData<String?>?) =
    bind(view, valueToBind) { viewToLoad, url -> viewToLoad.loadAvatar(url)}

@BindingAdapter("cover")
fun setCoverBinding(view: ImageView, valueToBind: LiveData<String?>?) =
    bind(view, valueToBind) { viewToLoad, url -> viewToLoad.loadCover(url) }

fun bind(view: ImageView, valueToBind: LiveData<String?>?, bingAction: (ImageView, url: String?) -> Unit) {
    valueToBind?.let { value ->
        view.parentActivity?.let { activity ->
            value.observe(activity, Observer { observedUrl ->
                bingAction(view, observedUrl)
            })
        }
    }
}