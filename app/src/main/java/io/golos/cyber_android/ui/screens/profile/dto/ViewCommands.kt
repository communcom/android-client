package io.golos.cyber_android.ui.screens.profile.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.domain.dto.UserDomain

class ShowSelectPhotoDialogCommand(val place: ProfileItem): ViewCommand

class ShowEditBioDialogCommand : ViewCommand

class MoveToSelectPhotoPageCommand(val place: ProfileItem, val imageUrl: String?): ViewCommand

class MoveToBioPageCommand(val text: String?): ViewCommand

class MoveToFollowersPageCommand(val filter: FollowersFilter, val mutualUsers: List<UserDomain>): ViewCommand

class MoveToLikedPageCommand : ViewCommand

class MoveToBlackListPageCommand : ViewCommand

class ShowSettingsDialogCommand : ViewCommand

class ShowExternalUserSettingsDialogCommand(val isBlocked: Boolean) : ViewCommand

class RestartAppCommand : ViewCommand

class LoadPostsAndCommentsCommand : ViewCommand

class MoveToWalletCommand : ViewCommand

