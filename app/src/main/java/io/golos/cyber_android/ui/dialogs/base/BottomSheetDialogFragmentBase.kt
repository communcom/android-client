package io.golos.cyber_android.ui.dialogs.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R

/**
 * Base class for all BottomSheetDialogs
 */
abstract class BottomSheetDialogFragmentBase<TResult> : BottomSheetDialogFragment() {
    protected lateinit var closeActionListener: (TResult?) -> Unit

    private var isItemSelected = false

    protected abstract val closeButton: View?

    protected abstract val layout: Int
        @LayoutRes get

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogFragment_RoundCorners)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeButton?.setOnClickListener { dismiss() }
        setupView()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(!isItemSelected) {
            closeActionListener(null)
        }
    }

    protected abstract fun setupView()

    protected fun closeOnItemSelected(item: TResult) {
        closeActionListener(item)
        isItemSelected = true
        dismiss()
    }
}