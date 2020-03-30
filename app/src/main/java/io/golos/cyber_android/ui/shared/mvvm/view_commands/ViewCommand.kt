package io.golos.cyber_android.ui.shared.mvvm.view_commands

import android.net.Uri
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.annotation.StringRes
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain
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
    val deepLinkUri: Uri?
) : NavigationCommand(navigationId, startDestination, graphId)

class NavigateToImageViewCommand(val imageUri: Uri) : ViewCommand

class NavigateToLinkViewCommand(val link: Uri) : ViewCommand

class NavigateToUserProfileCommand(val userId: UserIdDomain) : ViewCommand

class NavigateToProfileCommentMenuDialogViewCommand(val comment: Comment) : ViewCommand

class NavigateToPostCommand(
    val discussionIdModel: DiscussionIdModel,
    val contentId: ContentId
) : ViewCommand

class NavigateToNextScreen() : ViewCommand

class ShowPostRewardDialogCommand(@StringRes val titleResId: Int, @StringRes val textResId: Int) : ViewCommand

class ShowNoConnectionDialogCommand() : ViewCommand
class HideNoConnectionDialogCommand() : ViewCommand

class ShowUpdateAppDialogCommand() : ViewCommand