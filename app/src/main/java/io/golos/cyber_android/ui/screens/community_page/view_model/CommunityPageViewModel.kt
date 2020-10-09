package io.golos.cyber_android.ui.screens.community_page.view_model

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page.dto.*
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import io.golos.cyber_android.ui.screens.community_page.dto.NavigateToFriendsCommand
import io.golos.cyber_android.ui.screens.community_page.dto.NavigateToMembersCommand
import io.golos.cyber_android.ui.screens.community_page.dto.SwitchToLeadsTabCommand
import io.golos.cyber_android.ui.screens.community_page.mappers.CommunityPageDomainToCommunityPageMapper
import io.golos.cyber_android.ui.screens.community_page.model.CommunityPageModel
import io.golos.cyber_android.ui.shared.extensions.getMessage
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.cyber_android.ui.shared.utils.toLiveData
import io.golos.data.repositories.wallet.WalletRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.utils.helpers.EMPTY
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import io.golos.data.BuildConfig
import io.golos.domain.dto.CommunityPageDomain

class CommunityPageViewModel
@Inject constructor(private val appContext: Context, dispatchersProvider: DispatchersProvider, model: CommunityPageModel, private val currentUserRepositoryRead: CurrentUserRepositoryRead, private val communityId: CommunityIdDomain, private val walletRepository: WalletRepository) : ViewModelBase<CommunityPageModel>(dispatchersProvider, model) {

    private lateinit var communityPageDomain: CommunityPageDomain
    private val mRate = MutableLiveData<Double>()
    val rate: LiveData<Double>
        get() = mRate

    private lateinit var balance: List<WalletCommunityBalanceRecordDomain>

    val leaderBoardVisibility = MutableLiveData<Int>()

    private val _leaderBoardReportCount by lazy { MutableLiveData<Int>() }
    val leaderBoardReportCount: LiveData<Int>
        get() = _leaderBoardReportCount
    private val _leaderBoardProposalCount by lazy { MutableLiveData<Int>() }
    val leaderBoardProposalCount: LiveData<Int>
        get() = _leaderBoardProposalCount


    private val communityPageMutableLiveData = MutableLiveData<CommunityPage>()

    private val communityPageIsLoadProgressMutableLiveData = MutableLiveData(false)

    private val communityPageIsErrorMutableLiveData = MutableLiveData(false)

    val communityPageLiveData = communityPageMutableLiveData.toLiveData()

    val communityPageIsErrorLiveData = communityPageIsErrorMutableLiveData.toLiveData()

    val communityPageIsLoadProgressLiveData = communityPageIsLoadProgressMutableLiveData.toLiveData()

    private val _swipeRefreshing = MutableLiveData<Boolean>(false)
    val swipeRefreshing get() = _swipeRefreshing.toLiveData()

    fun start() {
        loadCommunityPage()
    }

    fun loadCommunityPage() {
        launch {
            try {
                communityPageIsErrorMutableLiveData.value = false
                communityPageIsLoadProgressMutableLiveData.value = true
                leaderBoardVisibility.value = View.GONE
                communityPageDomain = model.getCommunityPageById(communityId)

                val communityPage = CommunityPageDomainToCommunityPageMapper().invoke(communityPageDomain)
                leaderBoardVisibility.postValue(if (communityPage.isLeader) {
                    View.VISIBLE

                } else View.GONE)
                _leaderBoardReportCount.value = communityPageDomain.reportCount
                _leaderBoardProposalCount.value = communityPageDomain.proposalCount
                communityPageMutableLiveData.value = communityPage
                communityPageIsErrorMutableLiveData.value = false

                getRate(communityId)
                getBalance()

            } catch (e: Exception) {
                Timber.e(e)
                communityPageIsErrorMutableLiveData.value = true
            } finally {
                communityPageIsLoadProgressMutableLiveData.value = false
            }

        }
    }

    fun onConvertClick() {
        val isNewPoint = balance.any { it.communityId == communityId }
        val balances = balance as ArrayList<WalletCommunityBalanceRecordDomain>
        if (!isNewPoint) {
            balances.add(WalletCommunityBalanceRecordDomain(0.0, 0.0, communityPageMutableLiveData.value!!.avatarUrl, communityPageMutableLiveData.value!!.name, communityId, 0.0, 0))
        }
        _command.value = NavigateToWalletConvertCommand(communityId, balances)
    }

    fun onBackPressed() {
        _command.value = NavigateBackwardCommand()
    }

    fun onLeadsLabelClick() {
        _command.value = SwitchToLeadsTabCommand()
    }

    fun onLeaderSettingsClick() {
        communityPageMutableLiveData.value?.let {
            _command.value =
                ShowLeaderSettingsViewCommand(communityId, CommunityInfo(it.name, it.avatarUrl, it.membersCount, it.postsCount,communityPageDomain.reportCount,communityPageDomain.proposalCount))
        }
    }

    fun onCommunitySettingsClick() {
        _command.value = ShowCommunitySettings(communityPageMutableLiveData.value, currentUserRepositoryRead.userId.userId)
    }

    fun onMembersLabelClick() {
        _command.value = NavigateToMembersCommand(communityId)
    }

    fun onFriendsLabelClick() {
        _command.value = NavigateToFriendsCommand(communityPageMutableLiveData.value!!.friends)
    }

    fun changeJoinStatus() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                val communityPage = communityPageMutableLiveData.value
                val isSubscribed = communityPage?.isSubscribed ?: false
                if (isSubscribed) {
                    model.unsubscribeToCommunity(communityId)
                } else {
                    model.subscribeToCommunity(communityId)
                }
                communityPage?.isSubscribed = !isSubscribed
                communityPageMutableLiveData.value = communityPage
                communityPageIsErrorMutableLiveData.value = false
            } catch (e: Exception) {
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
                communityPageIsErrorMutableLiveData.value = true
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun onSwipeRefresh() {
        launch {
            loadCommunityPage()
            _swipeRefreshing.value = false
        }
    }

    fun hideCommunity() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.unsubscribeToCommunity(communityPageMutableLiveData.value!!.communityId)
                model.blockCommunity(communityPageMutableLiveData.value!!.communityId)

                val communityPage = communityPageMutableLiveData.value
                val isSubscribed = communityPage?.isSubscribed ?: false
                val isInBlackList = communityPage?.isInBlackList ?: false
                communityPage?.isSubscribed = !isSubscribed
                communityPage?.isInBlackList = !isInBlackList
                communityPageMutableLiveData.value = communityPage
                _command.value = ShowSuccessDialogViewCommand(communityPage?.name!!)
            } catch (e: Exception) {
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
                communityPageIsErrorMutableLiveData.value = true
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }

    }

    fun unHideCommunity() {
        launch {
            try {
                _command.value = SetLoadingVisibilityCommand(true)
                model.unBlockCommunity(communityPageMutableLiveData.value!!.communityId)

                val communityPage = communityPageMutableLiveData.value
                val isSubscribed = communityPage?.isSubscribed ?: false
                val isInBlackList = communityPage?.isInBlackList ?: false
                communityPage?.isSubscribed = !isSubscribed
                communityPage?.isInBlackList = !isInBlackList
                communityPageMutableLiveData.value = communityPage
                _command.value = ShowSuccessDialogViewCommand(communityPage?.name!!)
            } catch (e: Exception) {
                _command.value = ShowMessageTextCommand(e.getMessage(appContext))
                communityPageIsErrorMutableLiveData.value = true
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun getShareString(communityPage: CommunityPage): String {
        return "${BuildConfig.BASE_URL}/${communityPage.alias}"
    }

    private suspend fun getRate(communityId: CommunityIdDomain) {
        mRate.value = calculateRate(communityId)
    }

    private suspend fun calculateRate(communityId: CommunityIdDomain): Double = walletRepository.getExchangeRate(communityId)

    private suspend fun getBalance() {
        balance = walletRepository.getBalance()
    }

}