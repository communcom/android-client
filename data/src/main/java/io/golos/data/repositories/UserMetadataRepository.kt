package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.services.model.UserMetadataResult
import io.golos.data.api.UserMetadataApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.UserMetadataCollectionEntity
import io.golos.domain.entities.UserMetadataEntity
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.CyberToEntityMapper
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
class UserMetadataRepository(
    private val metadadataApi: UserMetadataApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
    private val toEntityMapper: CyberToEntityMapper<UserMetadataResult, UserMetadataEntity>
) : Repository<UserMetadataCollectionEntity, UserMetadataRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val savedMetadata = MutableLiveData<UserMetadataCollectionEntity>()
    private val metadataUpdateStates = MutableLiveData<Map<Identifiable.Id, QueryResult<UserMetadataRequest>>>()
    private val allRequest = UserMetadataFetchRequest(CyberName("alldata"))
    private val jobsMap = Collections.synchronizedMap(HashMap<UserMetadataRequest, Job>())

    override val allDataRequest: UserMetadataFetchRequest
        get() = allRequest

    override fun getAsLiveData(params: UserMetadataRequest): LiveData<UserMetadataCollectionEntity> {
        return savedMetadata
    }

    override fun makeAction(params: UserMetadataRequest) {
        repositoryScope.launch {
            try {
                metadataUpdateStates.value =
                    metadataUpdateStates.value.orEmpty() + (params.id to QueryResult.Loading(params))

                when (params) {
                    is UserMetadataFetchRequest -> {
                        val updatedMeta = withContext(dispatchersProvider.workDispatcher) {
                            metadadataApi.getUserMetadata(params.user)
                                .run { toEntityMapper(this) }
                        }
                        savedMetadata.value =
                            UserMetadataCollectionEntity(savedMetadata.value?.map.orEmpty() + (params.user to updatedMeta))
                    }

                    is UserMetadataUpdateRequest -> {
                        val updatedMeta = withContext(dispatchersProvider.workDispatcher) {
                            metadadataApi.setUserMetadata(
                                about = params.bio,
                                coverImage = params.coverImageUrl,
                                profileImage = params.profileImageUrl
                            )
                            delay(3_000)
                            metadadataApi.getUserMetadata(params.user)
                                .run { toEntityMapper(this) }
                        }

                        savedMetadata.value =
                            UserMetadataCollectionEntity(savedMetadata.value?.map.orEmpty() + (params.user to updatedMeta))
                    }
                }
                metadataUpdateStates.value =
                    metadataUpdateStates.value.orEmpty() + (params.id to QueryResult.Success(params as UserMetadataRequest))

            } catch (e: Exception) {
                logger(e)
                metadataUpdateStates.value =
                    metadataUpdateStates.value.orEmpty() + (params.id to QueryResult.Error(e, params))
            }
        }.let { job ->
            jobsMap[params]?.cancel()
            jobsMap[params] = job
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<UserMetadataRequest>>> =
        metadataUpdateStates
}