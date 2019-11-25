package io.golos.cyber_android.ui.screens.profile_photos.dto

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.PhotoPlace
import java.io.File

class InitPhotoPreviewCommand(val photoPlace: PhotoPlace, val imageUrl: String?, val isImageFromCamera: Boolean): ViewCommand

class StartCameraCommand : ViewCommand

class RequestResultImageCommand : ViewCommand

class PassResultCommand(val imageFile: File, val photoPlace: PhotoPlace): ViewCommand