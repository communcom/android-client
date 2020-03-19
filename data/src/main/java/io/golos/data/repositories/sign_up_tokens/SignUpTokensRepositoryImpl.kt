package io.golos.data.repositories.sign_up_tokens

import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.utils.await
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
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

    override suspend fun getGoogleAccessToken(authCode: String): String =
        processRequest(authCode) { code, client ->
            val requestBody: RequestBody = FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", config.googleAccessTokenClientId)
                .add("client_secret", config.googleAccessTokenClientSecret)
                .add("code", code)
                .build()

            val request: Request = Request.Builder()
                .url(config.googleAccessTokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build()

            return@processRequest withContext(dispatchersProvider.ioDispatcher) {
                val response = client.newCall(request).await()

                @Suppress("BlockingMethodInNonBlockingContext")
                val jsonObject = JSONObject(response.body!!.string())
                jsonObject["access_token"].toString()
            }
        }

    override suspend fun getGoogleIdentity(accessToken: String): String =
        processRequest(accessToken) { token, client ->
            val request: Request = Request.Builder()
                .url("${config.accessTokenBaseUrl}oauth/google-token?access_token=$token")
                .get()
                .build()

            return@processRequest withContext(dispatchersProvider.ioDispatcher) {
                val response = client.newCall(request).await()

                @Suppress("BlockingMethodInNonBlockingContext")
                val result = response.body!!.string()
                Timber.tag("ACCESS_TOKEN").d("identity: $result")
                result
//                val jsonObject = JSONObject(response.body!!.string())
//                jsonObject["access_token"].toString()
            }
        }


    private suspend fun <TS, TR>processRequest(sourceData: TS, action: suspend (TS, OkHttpClient) -> TR): TR {
        if(!networkStateChecker.isConnected) {
            throw SocketTimeoutException("No connection")
        }

        return action(sourceData, OkHttpClient())
    }
}