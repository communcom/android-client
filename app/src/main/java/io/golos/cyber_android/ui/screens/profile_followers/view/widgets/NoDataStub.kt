package io.golos.cyber_android.ui.screens.profile_followers.view.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_profile_followers_no_data_stub.view.*

class NoDataStub
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(getContext(), R.layout.view_profile_followers_no_data_stub, this)
    }

    fun setTitle(@StringRes titleResId: Int) {
        noDataTitle.text = context.resources.getString(titleResId)
    }
}