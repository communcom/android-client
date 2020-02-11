package io.golos.cyber_android.ui.screens.wallet_point.view

import android.os.Bundle
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentWalletPointBinding
import io.golos.cyber_android.ui.screens.wallet_point.di.WalletPointFragmentComponent
import io.golos.cyber_android.ui.screens.wallet_point.view_model.WalletPointViewModel
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain

class WalletPointFragment : FragmentBaseMVVM<FragmentWalletPointBinding, WalletPointViewModel>() {
    companion object {
        private const val BALANCE = "BALANCE"
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        fun newInstance(communityId: String, balance: List<WalletCommunityBalanceRecordDomain>) = WalletPointFragment().apply {
            arguments = Bundle().apply {
                putString(COMMUNITY_ID, communityId)
                putParcelableArray(BALANCE, balance.toTypedArray())
            }
        }
    }

    override fun provideViewModelType(): Class<WalletPointViewModel> = WalletPointViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_wallet_point

    override fun inject(key: String) =
        App.injections.get<WalletPointFragmentComponent>(
            key,
            GlobalConstants.PAGE_SIZE,
            arguments!!.getString(COMMUNITY_ID),
            arguments!!.getParcelableArray(BALANCE)!!.toList())
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<WalletPointFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentWalletPointBinding, viewModel: WalletPointViewModel) {
        binding.viewModel = viewModel
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        viewModel.myPointsItems.observe({ viewLifecycleOwner.lifecycle }) { myPointsArea.setItems(it, viewModel) }
//
//        viewModel.sendPointItems.observe({ viewLifecycleOwner.lifecycle }) {
//            sendPointsArea.setItems(viewModel.pageSize, it, viewModel) }
//
//        viewModel.historyItems.observe({ viewLifecycleOwner.lifecycle }) { setHistoryItems(viewModel.pageSize, it, viewModel) }
//
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }

    override fun processViewCommand(command: ViewCommand) {
        when (command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
        }
    }

//    private fun setHistoryItems(pageSize: Int, items: List<VersionedListItem>, listItemEventsProcessor: WalletHistoryListItemEventsProcessor) {
//        if(!::historyAdapter.isInitialized) {
//            historyLayoutManager = LinearLayoutManager(context)
//
//            historyAdapter = WalletHistoryAdapter(listItemEventsProcessor, pageSize)
//            historyAdapter.setHasStableIds(true)
//
//            historyList.isSaveEnabled = false
//            historyList.itemAnimator = null
//            historyList.layoutManager = historyLayoutManager
//            historyList.adapter = historyAdapter
//        }
//
//        historyAdapter.update(items)
//    }
}