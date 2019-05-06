package io.golos.domain.interactors.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.cyber4j.model.CyberName
import io.golos.domain.Repository
import io.golos.domain.entities.UserMetadataCollectionEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.UserMetadataFetchRequest
import io.golos.domain.requestmodel.UserMetadataRequest

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
class UserMetadataUseCase(
    protected val user: CyberName,
    protected val userMetadataRepository: Repository<UserMetadataCollectionEntity, UserMetadataRequest>
) : UseCase<QueryResult<UserMetadataModel>> {
    protected val observer = Observer<Any> {}
    protected val mediator = MediatorLiveData<Any>()

    protected val userMetadataLiveData = MutableLiveData<QueryResult<UserMetadataModel>>()
    protected val fetchMyUserRequest = UserMetadataFetchRequest(user)

    override val getAsLiveData: LiveData<QueryResult<UserMetadataModel>>
        get() = userMetadataLiveData

    init {
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
            val myUserUpdating =
                userMetadataRepository.updateStates.value.orEmpty()[fetchMyUserRequest.id] ?: return@addSource

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
    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(userMetadataRepository.getAsLiveData(userMetadataRepository.allDataRequest))
        mediator.removeSource(userMetadataRepository.updateStates)
    }
}