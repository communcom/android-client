package io.golos.data.api.image_upload

import android.net.Uri
import io.golos.commun4j.Commun4j
import io.golos.data.api.Commun4jApiBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.CurrentUserRepositoryRead
import java.io.File
import javax.inject.Inject

class ImageUploadApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead,
    private val dispatchersProvider: DispatchersProvider
) : Commun4jApiBase(commun4j, currentUserRepository), ImageUploadApi {

    /**
     * @param localUri - we need it only for mock
     * @return Uri of uploaded image on server
     */
    override suspend fun uploadImage(file: File, localUri: Uri): String {
        return commun4j.uploadImage(file).getOrThrow()
    }
}