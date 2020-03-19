package io.golos.data.repositories.sign_up_tokens

import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.utils.await
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.net.SocketTimeoutException
import javax.inject.Inject

class SignUpTokensRepositoryImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val networkStateChecker: NetworkStateChecker,
    private val config: SignUpTokensConfig
) : SignUpTokensRepository {

    override suspend fun getGoogleIdentity(accessToken: String): String = getIdentity(accessToken, "google")

    override suspend fun getFacebookIdentity(accessToken: String): String = getIdentity(accessToken, "facebook")

    private suspend fun getIdentity(accessToken: String, tokenType: String): String =
        processRequest(accessToken) { token, client ->
            val request: Request = Request.Builder()
                .url("${config.accessTokenBaseUrl}oauth/${tokenType}-token?access_token=$token")
                .get()
                .build()

            return@processRequest withContext(dispatchersProvider.ioDispatcher) {
                val response = client.newCall(request).await()

                @Suppress("BlockingMethodInNonBlockingContext")
                val result = response.body!!.string()
                Timber.tag("ACCESS_TOKEN").d("identity: $result")
                result
            }
        }

    private suspend fun <TS, TR>processRequest(sourceData: TS, action: suspend (TS, OkHttpClient) -> TR): TR {
        if(!networkStateChecker.isConnected) {
            throw SocketTimeoutException("No connection")
        }

        return action(sourceData, OkHttpClient())
    }
}



// {"identity":"118091995565154151673","provider":"google"}
// {"oauthState":"setUsername","identity":"118091995565154151673","provider":"google"}

//http://116.203.108.214:3000/oauth/facebook-token?access_token=<token>