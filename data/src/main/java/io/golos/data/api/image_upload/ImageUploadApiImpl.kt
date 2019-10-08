package io.golos.data.api.image_upload

import io.golos.commun4j.Commun4j
import io.golos.data.api.Commun4jApiBase
import io.golos.data.repositories.current_user_repository.CurrentUserRepositoryRead
import java.io.File
import javax.inject.Inject

class ImageUploadApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), ImageUploadApi {
    override fun uploadImage(file: File): String = commun4j.uploadImage(file).getOrThrow()
}