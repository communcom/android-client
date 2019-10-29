package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.services.model.AuthResult
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.utils.AuthUtils
import io.golos.commun4j.utils.StringSigner
import io.golos.data.api.user_metadata.UserMetadataApi
import io.golos.data.repositories.current_user_repository.CurrentUserRepository
import io.golos.data.toCyberName
import io.golos.data.toCyberUser
import io.golos.domain.*
import io.golos.domain.api.AuthApi
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.AuthType
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.UserKeyType
import io.golos.domain.extensions.distinctUntilChanged
import io.golos.domain.repositories.AuthStateRepository
import io.golos.domain.requestmodel.AuthRequest
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
@ApplicationScope
class AuthStateRepositoryImpl
@Inject
constructor(
    private val authApi: AuthApi,
    private val metadataApi: UserMetadataApi,
    private val dispatchersProvider: DispatchersProvider,
    private val keyValueStorage: KeyValueStorageFacade,
    private val userKeyStore: UserKeyStore,
    private val crashlytics: CrashlyticsFacade,
    private val currentUserRepository: CurrentUserRepository
) : AuthStateRepository {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val authRequestsLiveData = MutableLiveData<Map<Identifiable.Id, QueryResult<AuthRequest>>>()

    private val authState = MutableLiveData<AuthState>()

    private val authJobsMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())

    init {
        makeAction(getEmptyRequest(AuthType.SIGN_IN))
    }

    override fun getAsLiveData(params: AuthRequest): LiveData<AuthState> = authState

    override fun makeAction(params: AuthRequest) {
        lateinit var newParams: AuthRequest

        repositoryScope.launch {
            if (params.type == AuthType.LOG_OUT) {
                logout()
                authState.value = AuthState("", "".toCyberName(), false, false, false, false, AuthType.LOG_OUT)
                return@launch
            }

            newParams = if (params.isEmpty()) {
                loadAuthRequest(params.type)
            } else {
                params
            }

            if (newParams.isEmpty()) {
                authState.value =
                    AuthState("", "".toCyberName(), false, false, false, false, newParams.type)   // User is not logged in
                return@launch
            }

            if (authState.value == null) {
                authState.value = AuthState("", "".toCyberName(), false, false, false, false, newParams.type)
            } else if (authState.value?.isUserLoggedIn == true) {
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                        java.lang.IllegalStateException(
                            "user $newParams is logged in, reauth is not supported"
                        ), newParams
                    ))
                return@launch
            }

            authRequestsLiveData.value =
                authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Loading(newParams))

            try {
                withContext(dispatchersProvider.calculationsDispatcher) {
                    AuthUtils.checkPrivateWiF(newParams.activeKey)
                }
            } catch (e: IllegalArgumentException) {
                Timber.e(e)
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                        java.lang.IllegalArgumentException(
                            "wrong or malformed key"
                        ), newParams
                    ))
                return@launch
            }

            try {
                val account =
                    withContext(dispatchersProvider.ioDispatcher) {
                        try {
                            authApi.getUserAccount(newParams.user.userId.toCyberName())
                        } catch (ex: Exception) {
                            val userId = authApi.resolveCanonicalCyberName(newParams.userName)
                            newParams = AuthRequest(newParams.userName, userId.userId.toCyberUser(), newParams.activeKey, newParams.type)
                            authApi.getUserAccount(newParams.user.userId.toCyberName())
                        }
//                        if (newParams.user.userId.isEmpty()) {
//                            val userId = authApi.resolveCanonicalCyberName(newParams.userName)
//                            newParams =
//                                AuthRequest(newParams.userName, userId.userId.toCyberUser(), newParams.activeKey, newParams.type)
//                        }
//                        authApi.getUserAccount(newParams.user.userId.toCyberName())
                    }

                if (account.account_name.isEmpty()) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account ${newParams.user} not found"
                            ), newParams
                        ))
                    return@launch
                }

                val activeKey = withContext(dispatchersProvider.calculationsDispatcher) {
                    account.permissions.find { it.perm_name.compareTo("active") == 0 }
                }

                if (activeKey == null) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account  ${newParams.user} has no active permissions"
                            ), newParams
                        ))
                    return@launch
                }

                val publicActiveKeyFromServer = activeKey.required_auth.keys.firstOrNull()?.key

                if (publicActiveKeyFromServer == null) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                            java.lang.IllegalStateException(
                                "account  ${newParams.user} has no active permission key"
                            ), newParams
                        ))
                    return@launch
                }

                val isWiFsValid = withContext(dispatchersProvider.calculationsDispatcher) {
                    AuthUtils.isWiFsValid(newParams.activeKey, publicActiveKeyFromServer)
                }

                if (!isWiFsValid) {
                    authRequestsLiveData.value =
                        authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                            IllegalArgumentException("account keys not matches"), newParams
                        ))
                    return@launch
                }


            } catch (e: Exception) {
                Timber.e(e)
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(
                        e, newParams
                    ))
                return@launch
            }


            try {
                val authResult =
                    auth(newParams.userName, newParams.user.userId.toCyberName(), newParams.activeKey, newParams.type)!!
                withContext(dispatchersProvider.ioDispatcher) {
                    authApi.setActiveUserCreds(authResult.userId, newParams.activeKey)
                }

                onAuthSuccess(newParams.userName, authResult.userId, newParams.user, newParams.type)
            } catch (e: Exception) {
                Timber.e(e)
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() + (newParams.id to QueryResult.Error(e, newParams))
            }

            val rawAuthData = authRequestsLiveData.value.orEmpty()

            val copy = withContext(dispatchersProvider.calculationsDispatcher) {
                rawAuthData.toMutableMap()
                    .also {
                        it.values.map { queryResult ->
                            if (queryResult is QueryResult.Loading) QueryResult.Error(
                                IllegalStateException("only one auth request at a time may proceed"),
                                queryResult.originalQuery
                            )
                            else queryResult
                        }
                    }
            }

            authRequestsLiveData.value = copy

        }.let { job ->
            authJobsMap.entries.forEach {
                it.value?.cancel()
                authJobsMap[newParams.id] = job
            }
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<AuthRequest>>> =
        authRequestsLiveData.distinctUntilChanged()

    override val allDataRequest: AuthRequest
            by lazy {
                AuthRequest(
                    "destroyer2k",
                    "destroyer2k@golos".toCyberUser(),
                    "5JagnCwCrB2sWZw6zCvaBw51ifoQuNaKNsDovuGz96wU3tUw7hJ",
                    AuthType.SIGN_UP
                )
            }

    private suspend fun auth(userName: String, cyberName: CyberName, key: String, authType: AuthType): AuthResult? {
        Timber.tag(LogTags.LOGIN).d("Start auth. User: $userName, authType: $authType")

        return withContext(dispatchersProvider.ioDispatcher) {
            try {
                val secret = authApi.getAuthSecret()
                authApi.authWithSecret(
                    userName,
                    cyberName,
                    secret.secret,
                    StringSigner.signString(secret.secret, key)
                )
            } catch (e: Exception) {
                Timber.e(e)
                onAuthFail(e, authType)
                null
            }
        }
    }

    private suspend fun onAuthSuccess(userName: String, resolvedName: CyberName, originalName: CyberUser, authType: AuthType) {
        Timber.tag(LogTags.LOGIN).d("Auth success")

        val userMetadata = withContext(dispatchersProvider.ioDispatcher) {
            try {
                metadataApi.getUserMetadata(resolvedName)
            } catch (ex: Exception) {
                Timber.e(ex)
                null
            }
        }
        crashlytics.registerUser(userMetadata?.profile?.username ?: resolvedName.name, userMetadata?.profile?.userId?.name ?: originalName.userId)

        val loadingQuery = withContext(dispatchersProvider.calculationsDispatcher) {
            authRequestsLiveData.value?.entries?.find {
                true            // first and only record
//                val loadingUser = (it.value as? QueryResult.Loading)?.originalQuery?.user?.userId
//                loadingUser != null && (loadingUser == originalName.userId)
            }
        }

        val oldAuthState = withContext(dispatchersProvider.ioDispatcher) {
            keyValueStorage.getAuthState()
        }

        if (loadingQuery != null) {
            val finalAuthState = AuthState(
                userName,
                resolvedName,
                true,
                oldAuthState?.isPinCodeSettingsPassed ?: false,
                oldAuthState?.isFingerprintSettingsPassed ?: false,
                if (authType == AuthType.SIGN_IN) true else oldAuthState?.isKeysExported ?: false,
                authType
            )

            authState.value = finalAuthState
            currentUserRepository.authState = finalAuthState
            currentUserRepository.userAvatarUrl = userMetadata?.avatarUrl

            val originalLoadingQuery = loadingQuery.value as QueryResult.Loading
            authRequestsLiveData.value =
                authRequestsLiveData.value.orEmpty() + (loadingQuery.key to QueryResult.Success(
                    originalLoadingQuery.originalQuery
                ))

            withContext(dispatchersProvider.ioDispatcher) {
                keyValueStorage.saveAuthState(finalAuthState)
            }
        }
    }

    private fun onAuthFail(e: Exception, authType: AuthType) {
        Timber.tag(LogTags.LOGIN).d("Auth fail")

        repositoryScope.launch {
            authState.value = AuthState("", "".toCyberName(), false, false, false, false, authType)
            val loadingQuery =
                authRequestsLiveData.value?.entries?.findLast { it.value is QueryResult.Loading }

            if (loadingQuery != null) {
                authRequestsLiveData.value =
                    authRequestsLiveData.value.orEmpty() +
                            (loadingQuery.key to QueryResult.Error(
                                e, (loadingQuery.value as QueryResult.Loading).originalQuery
                            ))
            }
        }
    }

    private suspend fun loadAuthRequest(authType: AuthType): AuthRequest {
        val authSavedAuthState = withContext(dispatchersProvider.ioDispatcher) {
            keyValueStorage.getAuthState()
        }

        return if (authSavedAuthState?.isUserLoggedIn != true) {
            getEmptyRequest(authType)
        } else {
            val key = withContext(dispatchersProvider.ioDispatcher) {
                userKeyStore.getKey(UserKeyType.ACTIVE)
            }

            AuthRequest(authSavedAuthState.userName, authSavedAuthState.user.toCyberUser(), key, authType)
        }
    }

    private fun getEmptyRequest(type: AuthType) = AuthRequest("", CyberUser(""), "", type)

    private fun AuthRequest.isEmpty() = this.userName.isEmpty() && this.user.userId == "" && this.activeKey == ""

    private suspend fun logout() {
        withContext(dispatchersProvider.ioDispatcher) {
            val currentUser = keyValueStorage.getAuthState()!!.user

            keyValueStorage.removeAuthState()
            keyValueStorage.removePushNotificationsSettings(currentUser)
            keyValueStorage.removePinCode()
            keyValueStorage.removeAppUnlockWay()

            keyValueStorage.removeUserKey(UserKeyType.MEMO)
            keyValueStorage.removeUserKey(UserKeyType.POSTING)
            keyValueStorage.removeUserKey(UserKeyType.ACTIVE)
            keyValueStorage.removeUserKey(UserKeyType.OWNER)
        }
    }
}