package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.api.transactions.TransactionsApi
import io.golos.data.api.user_metadata.UserMetadataApi
import io.golos.data.errors.CyberToAppErrorMapper
import io.golos.domain.DispatchersProvider
import io.golos.domain.repositories.Repository
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.UserMetadataCollectionEntity
import io.golos.domain.entities.UserMetadataEntity
import io.golos.domain.requestmodel.*
import io.golos.domain.mappers.UserMetadataToEntityMapper
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
@ApplicationScope
class UserMetadataRepository
@Inject
constructor(
    private val metadataApi: UserMetadataApi,
    private val transactionsApi: TransactionsApi,
    private val dispatchersProvider: DispatchersProvider,
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
                        val pinResult = withContext(dispatchersProvider.calculationsDispatcher) {
                            val result = if (params.toPin)
                                metadataApi.pin(params.user)
                            else metadataApi.unPin(params.user)
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
                        val updatedMeta = withContext(dispatchersProvider.calculationsDispatcher) {
                            metadataApi.getUserMetadata(params.user)
                                .run { UserMetadataToEntityMapper.map(this.profile) }
                        }
                        savedMetadata.value =
                            UserMetadataCollectionEntity(savedMetadata.value?.map.orEmpty() + (params.user to updatedMeta))
                    }

                    is UserMetadataUpdateRequest -> {
                        val updatedMeta = withContext(dispatchersProvider.calculationsDispatcher) {
                            val transactionResult = metadataApi.setUserMetadata(
                                about = params.bio,
                                coverImage = params.coverImageUrl,
                                profileImage = params.profileImageUrl
                            )
                            if (params.shouldWaitForTransaction) {
                                transactionsApi.waitForTransaction(transactionResult.transaction_id)
                                metadataApi.getUserMetadata(params.user)
                                    .run { UserMetadataToEntityMapper.map(this.profile) }
                            } else {
                                //if we should not wait for transaction to complete, then we need to provide
                                //to user some response for his request. So we try to obtain previously saved
                                //metadata for this user (and if there is none, then using empty meta instead)
                                //this will be used ONLY in the registration process, where speed on the requests
                                //is important
                                val savedMeta = savedMetadata.value?.get(params.user) ?: UserMetadataEntity.empty
                                savedMeta.copy(
                                    personal = savedMeta.personal.copy(
                                        biography = params.bio ?: savedMeta.personal.biography,
                                        coverUrl = params.coverImageUrl ?: savedMeta.personal.coverUrl,
                                        avatarUrl = params.profileImageUrl ?: savedMeta.personal.avatarUrl
                                    )
                                )
                            }
                        }

                        savedMetadata.value =
                            UserMetadataCollectionEntity(savedMetadata.value?.map.orEmpty() + (params.user to updatedMeta))
                    }
                }
                metadataUpdateStates.value =
                    metadataUpdateStates.value.orEmpty() + (params.id to QueryResult.Success(params))

            } catch (e: Exception) {
                Timber.e(e)
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