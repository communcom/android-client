package io.golos.data.repositories.sign_up_tokens

import com.squareup.moshi.Moshi
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.utils.await
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.SignUpIdentityDomain
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
    private val config: SignUpTokensConfig,
    private val moshi: Moshi
) : SignUpTokensRepository {

    override suspend fun getGoogleIdentity(accessToken: String): SignUpIdentityDomain = getIdentity(accessToken, "google")

    override suspend fun getFacebookIdentity(accessToken: String): SignUpIdentityDomain = getIdentity(accessToken, "facebook")

    private suspend fun getIdentity(accessToken: String, tokenType: String): SignUpIdentityDomain =
        processRequest(accessToken) { token, client ->
            val request: Request = Request.Builder()
                .url("${config.accessTokenBaseUrl}oauth/${tokenType}-token?access_token=$token")
                .get()
                .build()

            @Suppress("BlockingMethodInNonBlockingContext")
            return@processRequest withContext(dispatchersProvider.ioDispatcher) {
                val response = client.newCall(request).await()

                val textResult = response.body!!.string()
                val result = moshi.adapter(SignUpIdentityDomain::class.java).fromJson(textResult)!!

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