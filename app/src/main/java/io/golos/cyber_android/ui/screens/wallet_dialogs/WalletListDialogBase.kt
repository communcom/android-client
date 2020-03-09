package io.golos.cyber_android.ui.screens.wallet_dialogs

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.recycler_view.ListAdapterBase
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.utils.id.IdUtil
import kotlinx.android.synthetic.main.dialog_wallet_items_list.*

/**
 * @param [TResult] is type of selected item key
 */
abstract class WalletListDialogBase<TResult, TAdapter: ListAdapterBase<*, VersionedListItem>> : BottomSheetDialogFragment() {

    protected lateinit var closeActionListener: (TResult?) -> Unit

    protected var isItemSelected = false

    private lateinit var listAdapter: TAdapter
    private lateinit var listLayoutManager: LinearLayoutManager

    private val injectionKey = IdUtil.generateStringId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogFragment_RoundCorners)

        inject(injectionKey)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setupDialog(dialog)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_wallet_items_list, container, false)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClose.setOnClickListener { dismiss() }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(!isItemSelected) {
            closeActionListener(null)
        }

        releaseInjection(injectionKey)
    }

    protected abstract fun provideAdapter(): TAdapter

    protected open fun inject(injectionKey: String) {}

    protected open fun releaseInjection(injectionKey: String) {}

    protected fun updateListData(items: List<VersionedListItem>) {
        if(!::listAdapter.isInitialized) {
            listLayoutManager = LinearLayoutManager(context)

            listAdapter = provideAdapter()
            listAdapter.setHasStableIds(true)

            itemsList.isSaveEnabled = false
            itemsList.itemAnimator = null
            itemsList.layoutManager = listLayoutManager
            itemsList.adapter = listAdapter
        }

        listAdapter.update(items)
    }

    private fun setupDialog(dialog: Dialog) {
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnShowListener {
            Handler().post {
                val bottomSheet = (dialog as? BottomSheetDialog)?.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }
}