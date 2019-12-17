package io.golos.cyber_android.ui.common.mvvm.view_commands

import android.net.Uri
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.annotation.StringRes
import io.golos.cyber_android.ui.dto.Comment

interface ViewCommand

class BackCommand : ViewCommand

data class NavigateToCommunityPageCommand(val communityId: String) : ViewCommand

class NavigateToCommunitiesListPageCommand : ViewCommand

class NavigateToInAppAuthScreenCommand : ViewCommand

class NavigateToMainScreenCommand : ViewCommand

class NavigateToSearchCommunitiesCommand : ViewCommand

class SetLoadingVisibilityCommand(val isVisible: Boolean) : ViewCommand

class ShowMessageCommand(@StringRes val textResId: Int) : ViewCommand

class ShowPostFiltersCommand : ViewCommand

class ShowConfirmationDialog(@StringRes val textRes: Int) : ViewCommand

class NavigationCommand(
    @IdRes val navigationId: Int?,
    @IdRes val startDestination: Int? = null,
    @NavigationRes val graphId: Int? = null
) : ViewCommand

class NavigateToImageViewCommand(val imageUri: Uri) : ViewCommand

class NavigateToLinkViewCommand(val link: Uri) : ViewCommand

class NavigateToUserProfileViewCommand(val userId: String) : ViewCommand

class NavigateToProfileCommentMenuDialogViewCommand(val comment: Comment) : ViewCommand