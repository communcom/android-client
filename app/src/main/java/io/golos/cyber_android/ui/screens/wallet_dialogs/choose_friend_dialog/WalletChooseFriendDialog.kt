package io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.wallet_dialogs.WalletListDialogBase
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.di.WalletChooseFriendDialogComponent
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.list.WalletChooseFriendDialogAdapter
import io.golos.cyber_android.ui.screens.wallet_dialogs.choose_friend_dialog.list.WalletChooseFriendDialogItemEventsProcessor
import io.golos.cyber_android.ui.screens.wallet_shared.send_points.list.data_source.SendPointsDataSource
import io.golos.cyber_android.ui.shared.helper.UIHelper
import io.golos.domain.DispatchersProvider
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserBriefDomain
import kotlinx.android.synthetic.main.dialog_wallet_items_list.*
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class WalletChooseFriendDialog : WalletListDialogBase<UserBriefDomain, WalletChooseFriendDialogAdapter>(), WalletChooseFriendDialogItemEventsProcessor, CoroutineScope {
    companion object {
        fun show(parent: Fragment, closeAction: (UserBriefDomain?) -> Unit) =
            WalletChooseFriendDialog()
                .apply { closeActionListener = closeAction }
                .show(parent.parentFragmentManager, "CHOOSE_FRIENDS")
    }

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

    override fun inject(injectionKey: String) {
        App.injections.get<WalletChooseFriendDialogComponent>(injectionKey).inject(this)
    }

    override fun releaseInjection(injectionKey: String) = App.injections.release<WalletChooseFriendDialogComponent>(injectionKey)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sendPointsDataSource.items.observe({viewLifecycleOwner.lifecycle}) { updateListData(it) }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvCommunitiesLabel.setText(R.string.send_points)

        launch {
            sendPointsDataSource.loadPage()
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

    override fun onItemClick(user: UserBriefDomain) {
        closeActionListener(user)
        isItemSelected = true
        dismiss()
    }

    override fun provideAdapter() = WalletChooseFriendDialogAdapter(this, GlobalConstants.PAGE_SIZE)
}