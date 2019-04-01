package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.model.IFramelyEmbedResult
import io.golos.cyber4j.model.OEmbedResult
import io.golos.data.api.EmbedApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.LinkEmbedResult
import io.golos.domain.model.EmbedRequest
import io.golos.domain.model.Identifiable
import io.golos.domain.model.QueryResult
import io.golos.domain.rules.CyberToEntityMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
class EmbedsRepository(
    private val embedApi: EmbedApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
    private val toEmbedMapperIframely: CyberToEntityMapper<IFramelyEmbedResult, LinkEmbedResult>,
    private val toEmbedMapperOembed: CyberToEntityMapper<OEmbedResult, LinkEmbedResult>
) : Repository<LinkEmbedResult, EmbedRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val savedEmbeds = HashMap<EmbedRequest, LiveData<LinkEmbedResult>>()
    private val embedUpdateStates = MutableLiveData<Map<Identifiable.Id, QueryResult<EmbedRequest>>>()

    private val allRequest = EmbedRequest("##all_data##")

    override val allDataRequest: EmbedRequest
        get() = allRequest

    override fun getAsLiveData(params: EmbedRequest): LiveData<LinkEmbedResult> {

        return savedEmbeds.getOrPut(params) { MutableLiveData<LinkEmbedResult>() }
    }

    override fun makeAction(params: EmbedRequest) {
        repositoryScope.launch {
            val liveData = getAsLiveData(params) as MutableLiveData

            embedUpdateStates.value = embedUpdateStates.value.orEmpty() + (params.id to QueryResult.Loading(params))

            var result: LinkEmbedResult? = null
            try {
                result = withContext(dispatchersProvider.workDispatcher) {
                    toEmbedMapperIframely(embedApi.getIframelyEmbed(params.url))
                }
            } catch (e: Exception) {
                logger(e)

                try {
                    result = withContext(dispatchersProvider.workDispatcher) {
                        toEmbedMapperOembed(embedApi.getOEmbedEmbed(params.url))
                    }
                } catch (e: java.lang.Exception) {
                    logger(e)
                    embedUpdateStates.value =
                        embedUpdateStates.value.orEmpty() + (params.id to QueryResult.Error(e, params))
                }

            }

            if (result != null) {
                embedUpdateStates.value =
                    embedUpdateStates.value.orEmpty() + (params.id to QueryResult.Success(params))
                liveData.value = result

            }
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<EmbedRequest>>>
        get() = embedUpdateStates
}