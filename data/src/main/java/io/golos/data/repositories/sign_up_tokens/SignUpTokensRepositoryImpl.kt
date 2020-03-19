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
import java.net.SocketTimeoutException
import javax.inject.Inject

class SignUpTokensRepositoryImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val networkStateChecker: NetworkStateChecker,
    private val config: SignUpTokensConfig
) : SignUpTokensRepository {

    override suspend fun getGoogleAccessToken(authCode: String): String {
        if(!networkStateChecker.isConnected) {
            throw SocketTimeoutException("No connection")
        }

        val httpClient = OkHttpClient()

        val requestBody: RequestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("client_id", config.googleAccessTokenClientId)
            .add("client_secret", config.googleAccessTokenClientSecret)
            .add("code", authCode)
            .build()

        val request: Request = Request.Builder()
            .url(config.googleAccessTokenUrl)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .post(requestBody)
            .build()

        return withContext(dispatchersProvider.ioDispatcher) {
            val response = httpClient.newCall(request).await()

            @Suppress("BlockingMethodInNonBlockingContext")
            val jsonObject = JSONObject(response.body!!.string())
            jsonObject["access_token"].toString()
        }
    }
}