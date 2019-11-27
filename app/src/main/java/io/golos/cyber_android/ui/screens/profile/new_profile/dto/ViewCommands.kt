package io.golos.cyber_android.ui.screens.profile.new_profile.dto

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.PhotoPlace

class ShowSelectPhotoDialogCommand(val place: PhotoPlace): ViewCommand

class MoveToSelectPhotoPageCommand(val place: PhotoPlace): ViewCommand

class MoveToAddBioPageCommand(): ViewCommand
