package io.golos.cyber_android.ui.shared_fragments.editor.model

import io.golos.cyber4j.services.model.OEmbedResult
import io.golos.cyber4j.sharedmodel.Either
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkError
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkType
import io.golos.data.api.EmbedApi
import io.golos.data.errors.CyberServicesError
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.withContext
import java.lang.UnsupportedOperationException
import javax.inject.Inject

class EditorPageModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val embedApi: EmbedApi
) : ModelBaseImpl(), EditorPageModel {

    override suspend fun getExternalLinkInfo(url: String): Either<ExternalLinkInfo, ExternalLinkError> =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val linkInfo = mapExternalLinkInfo(embedApi.getOEmbedEmbed(url), url)
                if(linkInfo == null) {
                    Either.Failure<ExternalLinkInfo, ExternalLinkError>(ExternalLinkError.TYPE_IS_NOT_SUPPORTED)
                } else {
                    Either.Success<ExternalLinkInfo, ExternalLinkError>(linkInfo)
                }
            } catch (ex: CyberServicesError) {
                App.logger.log(ex)
                Either.Failure<ExternalLinkInfo, ExternalLinkError>(ExternalLinkError.INVALID_URL)
            }
            catch (ex: Exception) {
                App.logger.log(ex)
                Either.Failure<ExternalLinkInfo, ExternalLinkError>(ExternalLinkError.GENERAL_ERROR)
            }
        }

    /**
     * @return null - this type of link is not supported
     */
    private fun mapExternalLinkInfo(serverLinkInfo: OEmbedResult, sourceUrl: String): ExternalLinkInfo? {
        val type  = when(serverLinkInfo.type) {
            "link" -> ExternalLinkType.WEBSITE
            "photo" -> ExternalLinkType.IMAGE
            "video" -> ExternalLinkType.VIDEO
            else -> {
                App.logger.log(UnsupportedOperationException("This resource type is not supported: ${serverLinkInfo.type}"))
                null
            }
        }
        ?: return null

        return ExternalLinkInfo(
            type,
            serverLinkInfo.description,
            serverLinkInfo.title,
            serverLinkInfo.thumbnail_url,
            sourceUrl)
    }
}