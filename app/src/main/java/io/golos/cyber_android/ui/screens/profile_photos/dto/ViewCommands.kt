package io.golos.cyber_android.ui.screens.profile_photos.dto

import io.golos.cyber_android.ui.dto.ProfileItem
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import java.io.File

class InitPhotoPreviewCommand(val profileItem: ProfileItem, val imageUrl: String?, val isImageFromCamera: Boolean): ViewCommand

class StartCameraCommand : ViewCommand

class RequestResultImageCommand : ViewCommand

class PassResultCommand(val imageFile: File, val profileItem: ProfileItem): ViewCommand