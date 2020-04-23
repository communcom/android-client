package io.golos.cyber_android.ui.screens.profile.view.binding_adapters

import android.annotation.SuppressLint
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.shared.glide.loadAvatar
import io.golos.cyber_android.ui.shared.glide.loadCover
import io.golos.cyber_android.ui.shared.utils.toMMMM_DD_YYYY_Format
import io.golos.utils.format.KiloCounterFormatter
import java.util.*

@BindingAdapter("avatar")
fun setAvatarBinding(view: ImageView, valueToBind: LiveData<String?>?) =
    valueToBind?.value?.let { view.loadAvatar(it) }

@BindingAdapter("cover")
fun setCoverBinding(view: ImageView, valueToBind: LiveData<String?>?) =
    valueToBind?.value?.let { view.loadCover(it) }

@SuppressLint("SetTextI18n")
@BindingAdapter("joined")
fun setJoinedDateBinding(view: TextView, valueToBind: LiveData<Date>?) =
    valueToBind?.value?.let { view.text = "${view.context.resources.getString(R.string.joined)} ${it.toMMMM_DD_YYYY_Format()}" }

@BindingAdapter("kilo")
fun setKiloBinding(view: TextView, valueToBind: LiveData<Int>?) =
    valueToBind?.value?.let { view.text = KiloCounterFormatter.format(it) }

@BindingAdapter("communitiesVisibility")
fun setCommunitiesVisibility(view: FrameLayout, valueToBind: LiveData<ProfileCommunities?>?) =
    valueToBind?.value.let {
        view.visibility = if(it != null && it.highlightCommunities.isNotEmpty()) View.VISIBLE else View.GONE
    }