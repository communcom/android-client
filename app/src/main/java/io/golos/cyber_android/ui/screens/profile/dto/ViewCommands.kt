package io.golos.cyber_android.ui.screens.profile.dto

import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.UserDomain
import io.golos.domain.dto.notifications.NotificationSettingsDomain

class ShowSelectPhotoDialogCommand(val place: ProfileItem): ViewCommand

class ShowEditBioDialogCommand : ViewCommand

class NavigateToSelectPhotoPageCommand(val place: ProfileItem, val imageUrl: String?): ViewCommand

class NavigateToBioPageCommand(val text: String?): ViewCommand

class NavigateToFollowersPageCommand(val filter: FollowersFilter, val mutualUsers: List<UserDomain>): ViewCommand

class NavigateToLikedPageCommand : ViewCommand

class NavigateToBlackListPageCommand : ViewCommand

class ShowNotificationsSettingsDialogCommand(val sourceNotifications: List<NotificationSettingsDomain>) : ViewCommand

class ShowSettingsDialogCommand : ViewCommand

class ShowExternalUserSettingsDialogCommand(val isBlocked: Boolean) : ViewCommand

class RestartAppCommand : ViewCommand

class LoadPostsAndCommentsCommand : ViewCommand

class NavigateToWalletBackCommand() : ViewCommand

class NavigateToHomeBackCommand() : ViewCommand

