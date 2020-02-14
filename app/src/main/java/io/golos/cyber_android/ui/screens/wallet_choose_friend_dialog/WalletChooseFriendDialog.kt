package io.golos.cyber_android.ui.screens.wallet_choose_friend_dialog

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
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.wallet_choose_friend_dialog.di.WalletChooseFriendDialogComponent
import io.golos.cyber_android.ui.screens.wallet_choose_friend_dialog.list.WalletChooseFriendDialogAdapter
import io.golos.cyber_android.ui.screens.wallet_choose_friend_dialog.list.WalletChooseFriendDialogItemEventsProcessor
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.data_source.SendPointsDataSource
import io.golos.cyber_android.ui.shared.helper.UIHelper
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.utils.IdUtil
import kotlinx.android.synthetic.main.dialog_wallet_items_list.*
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WalletChooseFriendDialog : BottomSheetDialogFragment(), WalletChooseFriendDialogItemEventsProcessor, CoroutineScope {
    companion object {
        fun newInstance(closeAction: (UserIdDomain?) -> Unit) =
            WalletChooseFriendDialog()
                .apply {
                    closeActionListener = closeAction
                }
        }

    private lateinit var  closeActionListener: (UserIdDomain?) -> Unit

    private var isUserSelected = false

    private lateinit var listAdapter: WalletChooseFriendDialogAdapter
    private lateinit var listLayoutManager: LinearLayoutManager

    private val injectionKey = IdUtil.generateStringId()

    @Inject
    internal lateinit var sendPointsDataSource: SendPointsDataSource

    @Inject
    internal lateinit var dispatchersProvider: DispatchersProvider

    @Inject
    internal lateinit var uiHelper: UIHelper

    private val errorHandler = CoroutineExceptionHandler { _, ex ->
        Timber.e(ex)
        uiHelper.showMessage(R.string.common_general_error)
    }

    private val scopeJob: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext by lazy { scopeJob + dispatchersProvider.uiDispatcher + errorHandler }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogFragment_RoundCorners)

        App.injections.get<WalletChooseFriendDialogComponent>(injectionKey).inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        setupDialog(dialog)

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sendPointsDataSource.items.observe({viewLifecycleOwner.lifecycle}) { updateList(it) }
        return inflater.inflate(R.layout.dialog_wallet_items_list, container, false)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClose.setOnClickListener { dismiss() }
        tvCommunitiesLabel.setText(R.string.send_points)

        launch {
            sendPointsDataSource.loadPage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        scopeJob.cancel()

        if(!isUserSelected) {
            closeActionListener(null)
        }

        App.injections.release<WalletChooseFriendDialogComponent>(injectionKey)
    }

    private fun updateList(items: List<VersionedListItem>) {
        if(!::listAdapter.isInitialized) {
            listLayoutManager = LinearLayoutManager(context)

            listAdapter = WalletChooseFriendDialogAdapter(this, GlobalConstants.PAGE_SIZE)
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

    override fun onNextPageReached() {
        launch {
            sendPointsDataSource.loadPage()
        }
    }

    override fun onRetryClick() {
        launch {
            sendPointsDataSource.retry()
        }
    }

    override fun onItemClick(userId: UserIdDomain) {
        closeActionListener(userId)
        isUserSelected = true
        dismiss()
    }
}