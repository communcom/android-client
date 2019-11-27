package io.golos.cyber_android.ui.screens.profile.new_profile.dto

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.ProfileItem

class ShowSelectPhotoDialogCommand(val place: ProfileItem): ViewCommand

class ShowEditBioDialogCommand(): ViewCommand

class MoveToSelectPhotoPageCommand(val place: ProfileItem): ViewCommand

class MoveToBioPageCommand(val text: String?): ViewCommand
