package io.golos.cyber_android.ui.shared.mvvm.view_commands

import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.annotation.StringRes
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.cyber_android.ui.dto.Post
import io.golos.domain.dto.RewardCurrency
import io.golos.domain.dto.*
import io.golos.domain.use_cases.model.DiscussionIdModel

interface ViewCommand

class NavigateBackwardCommand: ViewCommand
class NavigateForwardCommand: ViewCommand

class HideKeyboardCommand(): ViewCommand

data class NavigateToCommunityPageCommand(val communityId: CommunityIdDomain) : ViewCommand

class NavigateToCommunitiesListPageCommand(val userId: UserIdDomain) : ViewCommand

class NavigateToInAppAuthScreenCommand(
    val isPinCodeUnlockEnabled: Boolean,
    @StringRes val pinCodeHeaderText: Int? = null,
    @StringRes val fingerprintHeaderText: Int? = null
) : ViewCommand

class NavigateToMainScreenCommand : ViewCommand

class NavigationToParentScreenWithStringResultCommand(val permlink: String) : ViewCommand

class NavigateToSearchCommunitiesCommand : ViewCommand

class SetLoadingVisibilityCommand(val isVisible: Boolean) : ViewCommand

class ShowMessageResCommand(@StringRes val textResId: Int, val isError: Boolean = true): ViewCommand
class ShowMessageTextCommand(val text: String, val isError: Boolean = true): ViewCommand

class ShowPostFiltersCommand : ViewCommand

class ShowConfirmationDialog(@StringRes val textRes: Int) : ViewCommand

class HideSoftKeyboardCommand: ViewCommand

open class NavigationCommand(
    @IdRes val navigationId: Int?,
    @IdRes val startDestination: Int? = null,
    @NavigationRes val graphId: Int? = null
) : ViewCommand

class NavigationToDashboardCommand(
    @IdRes navigationId: Int?,
    @IdRes startDestination: Int? = null,
    @NavigationRes graphId: Int? = null,
    val intent: Intent?
) : NavigationCommand(navigationId, startDestination, graphId)

class NavigateToImageViewCommand(val imageUri: Uri) : ViewCommand

class NavigateToLinkViewCommand(val link: Uri) : ViewCommand

class NavigateToUserProfileCommand(val userId: UserIdDomain) : ViewCommand

class ScrollProfileToTopCommand : ViewCommand

class NavigateToProfileCommentMenuDialogViewCommand(val comment: Comment) : ViewCommand

class NavigateToPostCommand(
    val discussionIdModel: DiscussionIdModel,
    val contentId: ContentIdDomain
) : ViewCommand

class NavigateToNextScreen() : ViewCommand

class ShowPostRewardDialogCommand(@StringRes val titleResId: Int, @StringRes val textResId: Int) : ViewCommand

class ViewInExplorerViewCommand(val exploreUrl:String) : ViewCommand

data class SelectRewardCurrencyDialogCommand(val startCurrency: RewardCurrency) : ViewCommand

class ShowNoConnectionDialogCommand() : ViewCommand
class HideNoConnectionDialogCommand() : ViewCommand

class ShowUpdateAppDialogCommand() : ViewCommand

class NavigateToWalletCommand(val balance: List<WalletCommunityBalanceRecordDomain>) : ViewCommand

data class NavigateToDonateCommand(
    val contentId: ContentIdDomain,
    val communityId: CommunityIdDomain,
    val contentAuthor: UserBriefDomain,
    val balance: List<WalletCommunityBalanceRecordDomain>,
    val amount: Double?
) : ViewCommand {
    companion object {
        fun build(
            donate: DonateType,
            contentId: ContentIdDomain,
            communityId: CommunityIdDomain,
            contentAuthor: UserBriefDomain,
            balance: List<WalletCommunityBalanceRecordDomain>): NavigateToDonateCommand {

            val amount = when(donate) {
                DonateType.DONATE_10 -> 10.0
                DonateType.DONATE_100 -> 100.0
                DonateType.DONATE_1000 -> 1000.0
                DonateType.DONATE_OTHER -> null
            }

            return NavigateToDonateCommand(
                contentId = contentId,
                communityId = communityId,
                contentAuthor = contentAuthor,
                balance = balance,
                amount = amount
            )
        }
    }
}

data class ShowDonationUsersDialogCommand(val donation: DonationsDomain) :ViewCommand