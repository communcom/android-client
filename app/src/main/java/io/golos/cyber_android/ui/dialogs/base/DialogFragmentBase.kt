package io.golos.cyber_android.ui.dialogs.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import io.golos.cyber_android.R

/**
 * Base class for all dialogs
 */
abstract class DialogFragmentBase : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFragment_RoundCorners)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(provideLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    @LayoutRes
    protected abstract fun provideLayout(): Int

    protected abstract fun setupView()

    protected fun View.setSelectAction(resultCode: Int, putArgsAction: Intent.() -> Unit = {}) {
        this.setOnClickListener {
            targetFragment?.onActivityResult(targetRequestCode, resultCode, Intent().also {
                putArgsAction(it)
            })
            dismiss()
        }
    }

    protected fun setSelectAction(resultCode: Int, putArgsAction: Intent.() -> Unit = {}) {
        targetFragment?.onActivityResult(targetRequestCode, resultCode, Intent().also { intent ->
            putArgsAction.invoke(intent)
        })
        dismiss()
    }
}