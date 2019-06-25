package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.model.CyberName
import io.golos.cyber4j.services.model.UserMetadataResult
import io.golos.data.api.TransactionsApi
import io.golos.data.api.UserMetadataApi
import io.golos.data.errors.CyberToAppErrorMapper
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
    private val transactionsApi: TransactionsApi,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger,
    private val toEntityMapper: CyberToEntityMapper<UserMetadataResult, UserMetadataEntity>,
    private val toAppErrorMapper: CyberToAppErrorMapper
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

                    is FollowUserRequest -> {
                        val pinResult = withContext(dispatchersProvider.workDispatcher) {
                            val result = if (params.toPin)
                                metadadataApi.pin(params.user)
                            else metadadataApi.unPin(params.user)
                            transactionsApi.waitForTransaction(result.first.transaction_id)
                            result.second
                        }

                        //after successful pin request we should update stats of both pinner and pinned users
                        //we can send 2 network requests, but for the sake of optimisation we just increase/decrease both users stats
                        //depending on [params.toPin] value
                        savedMetadata.value = UserMetadataCollectionEntity(
                            savedMetadata.value?.map.orEmpty().run {
                                val mutable = this.toMutableMap()
                                mutable[pinResult.pinner]?.let {
                                    mutable[pinResult.pinner] = it.copy(
                                        subscriptions = it.subscriptions.copy(
                                            usersCount = it.subscriptions.usersCount +
                                                    if (params.toPin) 1 else -1
                                        )
                                    )
                                }

                                mutable[pinResult.pinning]?.let {
                                    mutable[pinResult.pinning] = it.copy(
                                        subscribers = it.subscribers.copy(
                                            usersCount = it.subscribers.usersCount +
                                                    if (params.toPin) 1 else -1
                                        ),
                                        isSubscribed = params.toPin
                                    )
                                }

                                mutable
                            }
                        )


                    }

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
                            val transactionResult = metadadataApi.setUserMetadata(
                                about = params.bio,
                                coverImage = params.coverImageUrl,
                                profileImage = params.profileImageUrl
                            )
                            transactionsApi.waitForTransaction(transactionResult.transaction_id)
                            metadadataApi.getUserMetadata(params.user)
                                .run { toEntityMapper(this) }
                        }

                        savedMetadata.value =
                            UserMetadataCollectionEntity(savedMetadata.value?.map.orEmpty() + (params.user to updatedMeta))
                    }
                }
                metadataUpdateStates.value =
                    metadataUpdateStates.value.orEmpty() + (params.id to QueryResult.Success(params))

            } catch (e: Exception) {
                logger(e)
                metadataUpdateStates.value =
                    metadataUpdateStates.value.orEmpty() + (params.id to QueryResult.Error(
                        toAppErrorMapper.mapIfNeeded(e),
                        params
                    ))
            }
        }.let { job ->
            jobsMap[params]?.cancel()
            jobsMap[params] = job
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<UserMetadataRequest>>> =
        metadataUpdateStates
}