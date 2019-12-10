package io.golos.cyber_android.ui.common.mvvm.view_commands

import androidx.annotation.StringRes

interface ViewCommand

class BackCommand: ViewCommand

data class NavigateToCommunityPageCommand(val communityId: String) : ViewCommand

class NavigateToCommunitiesListPageCommand() : ViewCommand

class NavigateToInAppAuthScreenCommand : ViewCommand

class NavigateToMainScreenCommand: ViewCommand

class NavigateToSearchCommunitiesCommand : ViewCommand

class SetLoadingVisibilityCommand(val isVisible: Boolean) : ViewCommand

class ShowMessageCommand(@StringRes val textResId: Int): ViewCommand

class ShowPostFiltersCommand : ViewCommand

class ShowConfirmationDialog(@StringRes val textRes: Int) : ViewCommand