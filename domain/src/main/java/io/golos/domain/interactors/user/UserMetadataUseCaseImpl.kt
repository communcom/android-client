package io.golos.domain.interactors.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.Repository
import io.golos.domain.entities.UserMetadataCollectionEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
class UserMetadataUseCaseImpl
@Inject
constructor (
    private val user: CyberName,
    private val userMetadataRepository: Repository<UserMetadataCollectionEntity, UserMetadataRequest>
) : UseCase<QueryResult<UserMetadataModel>>, UserMetadataUseCase {
    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    private val userMetadataLiveData = MutableLiveData<QueryResult<UserMetadataModel>>()
    private val fetchMyUserRequest = UserMetadataFetchRequest(user)

    private val metadataUpdateResultLiveData = MutableLiveData<QueryResult<UserMetadataRequest>>()
    override val getUpdateResultLiveData: LiveData<QueryResult<UserMetadataRequest>> = metadataUpdateResultLiveData

    private var lastUpdateRequestID: Identifiable.Id? = null

    override val getAsLiveData: LiveData<QueryResult<UserMetadataModel>>
        get() = userMetadataLiveData

    init {
        requestRefresh()
    }

    override fun requestRefresh() {
        userMetadataRepository.makeAction(fetchMyUserRequest)
    }

    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(userMetadataRepository.getAsLiveData(userMetadataRepository.allDataRequest)) {
        }
        mediator.addSource(userMetadataRepository.updateStates) {
            println("updating")
            val myUser = userMetadataRepository.getAsLiveData(userMetadataRepository.allDataRequest)
                .value
                ?.map
                ?.values?.find { it.userId.name == user.name || it.username == user.name }
                ?.run {
                    UserMetadataModel(
                        UserPersonalDataModel(
                            personal.avatarUrl, personal.coverUrl, personal.biography,
                            ContactsModel(
                                personal.contacts?.facebook, personal.contacts?.telegram, personal.contacts?.whatsApp,
                                personal.contacts?.weChat
                            )

                        ), UserSubscriptionsModel(subscriptions.usersCount, subscriptions.communitiesCount)
                        , UserStatsModel(stats.postsCount, stats.commentsCount)
                        , userId
                        , username
                        , SubscribersModel(subscribers.usersCount, subscribers.communitiesCount)
                        , createdAt
                        , isSubscribed
                    )
                }

            userMetadataRepository.updateStates.value.orEmpty()[fetchMyUserRequest.id]?.let { myUserUpdating ->
                val resultingState = when (myUserUpdating) {
                    is QueryResult.Error -> QueryResult.Error(myUserUpdating.error, myUser ?: UserMetadataModel.empty)
                    is QueryResult.Loading -> QueryResult.Loading(myUser ?: UserMetadataModel.empty)
                    is QueryResult.Success -> QueryResult.Success(
                        myUser ?: throw IllegalStateException(
                            "successfully fetched user is not in repository, " +
                                    "this should not happen "
                        )
                    )
                }
                userMetadataLiveData.value = resultingState
            }

            userMetadataRepository.updateStates.value.orEmpty()[lastUpdateRequestID]?.let { myUserUpdating ->
                metadataUpdateResultLiveData.value = myUserUpdating
            }
        }
    }

    override fun updateMetadata(
        newBio: String?,
        newCoverUrl: String?,
        newProfileImageUrl: String?,
        shouldWaitForTransaction: Boolean
    ) {
        val request = UserMetadataUpdateRequest(user, shouldWaitForTransaction, newBio, newCoverUrl, newProfileImageUrl)
        lastUpdateRequestID = request.id
        userMetadataRepository.makeAction(request)
    }

    override fun switchFollowingStatus() {
        (userMetadataLiveData.value as? QueryResult.Success)?.originalQuery?.let { metadata ->
            val request = FollowUserRequest(user, !metadata.isSubscribed)
            lastUpdateRequestID = request.id
            userMetadataRepository.makeAction(request)
        }
    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(userMetadataRepository.getAsLiveData(userMetadataRepository.allDataRequest))
        mediator.removeSource(userMetadataRepository.updateStates)
    }
}