package io.golos.cyber_android.ui.shared.widgets.lists

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_no_data_stub.view.*

class NoDataStub
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(getContext(), R.layout.view_no_data_stub, this)
    }

    fun setTitle(@StringRes titleResId: Int) {
        noDataTitle.text = context.resources.getString(titleResId)
    }

    fun setExplanation(@StringRes explanationResIdd: Int) {
        noDataExplanation.text = context.resources.getString(explanationResIdd)
    }
}