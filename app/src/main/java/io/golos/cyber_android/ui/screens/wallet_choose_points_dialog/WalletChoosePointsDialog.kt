package io.golos.cyber_android.ui.screens.wallet_choose_points_dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.wallet.dto.MyPointsListItem
import io.golos.cyber_android.ui.screens.wallet_choose_points_dialog.list.WalletChoosePointsDialogAdapter
import io.golos.cyber_android.ui.screens.wallet_choose_points_dialog.list.WalletChoosePointsDialogItemEventsProcessor
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.domain.utils.IdUtil
import kotlinx.android.synthetic.main.dialog_wallet_items_list.*

class WalletChoosePointsDialog : BottomSheetDialogFragment(), WalletChoosePointsDialogItemEventsProcessor {
    companion object {
        private const val BALANCE = "BALANCE"

        fun show(parent: Fragment, balance: List<WalletCommunityBalanceRecordDomain>, closeAction: (String?) -> Unit) =
            WalletChoosePointsDialog()
                .apply {
                    closeActionListener = closeAction
                    arguments = Bundle().apply {
                        putParcelableArray(BALANCE, balance.toTypedArray())
                    }
                }
                .show(parent.parentFragmentManager, "CHOOSE_POINTS")
    }

    private lateinit var  closeActionListener: (String?) -> Unit

    private var isCommunitySelected = false

    private lateinit var listAdapter: WalletChoosePointsDialogAdapter
    private lateinit var listLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogFragment_RoundCorners)
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
        updateList(arguments!!.getParcelableArray(BALANCE)!!.toList() as List<WalletCommunityBalanceRecordDomain>)
    }

    override fun onDestroy() {
        super.onDestroy()

        if(!isCommunitySelected) {
            closeActionListener(null)
        }
    }

    private fun updateList(balance: List<WalletCommunityBalanceRecordDomain>) {
        if(!::listAdapter.isInitialized) {
            listLayoutManager = LinearLayoutManager(context)

            listAdapter = WalletChoosePointsDialogAdapter(this)
            listAdapter.setHasStableIds(true)

            itemsList.isSaveEnabled = false
            itemsList.itemAnimator = null
            itemsList.layoutManager = listLayoutManager
            itemsList.adapter = listAdapter
        }

        val listData = balance.map { MyPointsListItem(IdUtil.generateLongId(), 0, false, false, false, it) }
        listAdapter.update(listData)
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

    override fun onItemClick(communityId: String) {
        closeActionListener(communityId)
        isCommunitySelected = true
        dismiss()
    }
}