package io.golos.cyber_android.application.di

import android.app.Application
import android.content.Context
import android.os.Build
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.commun4j.sharedmodel.SocketOpenQueryParams
import io.golos.cyber_android.BuildConfig
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacadeImpl
import io.golos.cyber_android.application.shared.analytics.modules.AnalyticsModule
import io.golos.cyber_android.application.shared.analytics.modules.amplitude.AmplitudeAnalyticsModule
import io.golos.cyber_android.application.shared.analytics.modules.debug.DebugAnalyticsModule
import io.golos.cyber_android.application.shared.logger.TimberTreeDebug
import io.golos.cyber_android.application.shared.logger.TimberTreeRelease
import io.golos.cyber_android.application.shared.logger.Cyber4JLogger
import io.golos.cyber_android.application.shared.logger.TimberTreeChecking
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.data.encryption.aes.EncryptorAES
import io.golos.data.encryption.aes.EncryptorAESOldApi
import io.golos.data.repositories.sign_up_tokens.SignUpTokensConfig
import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.utils.id.IdUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.io.File
import java.lang.UnsupportedOperationException
import java.nio.charset.Charset
import javax.inject.Named

/** Application level module - global objects are created here   */
@Module
class AppModule(
    private val app: Application
) {
    @Provides
    @ApplicationScope
    internal fun provideApplication(): Application = app

    @Provides
    @ApplicationScope
    internal fun provideContext(): Context = app.applicationContext

    @Provides
    @ApplicationScope
    @Named(Clarification.AES)
    internal fun provideEncryptor(
        keyValueStorageFacade: KeyValueStorageFacade,
        @Named(Clarification.RSA) encryptor: Encryptor
    ): Encryptor {

        return if (Build.VERSION.SDK_INT >= 23) {
            EncryptorAES()
        } else {
            EncryptorAESOldApi(keyValueStorageFacade, encryptor)
        }
    }

    @Provides
    @ApplicationScope
    internal fun provideCommun4JConfig(): Commun4jConfig {
        val instanceIdFile = File(app.applicationContext.filesDir, "instance.id")
        val instanceId = if(instanceIdFile.exists()) {
            instanceIdFile.readLines(Charsets.UTF_8)[0]
        } else {
            val id = IdUtil.generateStringId()
            instanceIdFile.writeText(id, Charsets.UTF_8)
            id
        }

        Timber.tag("FCM_MESSAGES").d("DeviceId: $instanceId")

        return Commun4jConfig(
            blockChainHttpApiUrl = BuildConfig.BLOCK_CHAIN_HTTP_API_URL,
            servicesUrl = BuildConfig.SERVICES_URL,
            httpLogger = Cyber4JLogger(Cyber4JLogger.HTTP),
            socketLogger = Cyber4JLogger(Cyber4JLogger.SOCKET),
            socketOpenQueryParams = SocketOpenQueryParams(
                version = BuildConfig.VERSION_NAME,
                deviceType = GlobalConstants.DEVICE_TYPE,
                platform = GlobalConstants.PLATFORM,
                clientType = GlobalConstants.CLIENT_TYPE,
                deviceId = instanceId
            )
        )
    }

    @Provides
    internal fun provideSignUpTokensConfig(): SignUpTokensConfig =
        SignUpTokensConfig(
            accessTokenBaseUrl = BuildConfig.ACCESS_TOKEN_BASE_URL
        )

    @Provides
    internal fun provideDispatchersProvider(): DispatchersProvider = object : DispatchersProvider {
        override val uiDispatcher: CoroutineDispatcher
            get() = Dispatchers.Main
        override val calculationsDispatcher: CoroutineDispatcher
            get() = Dispatchers.Default
        override val ioDispatcher: CoroutineDispatcher
            get() = Dispatchers.IO
    }

    @Provides
    internal fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    internal fun provideTimberTree(crashlytics: CrashlyticsFacade): Timber.Tree =
        when(BuildConfig.FLAVOR) {
            "dev" -> TimberTreeDebug()
            "prod" -> TimberTreeRelease(crashlytics)
            "checking" -> TimberTreeChecking()
            else -> throw UnsupportedOperationException("This flavor is not supported: ${BuildConfig.FLAVOR}")
        }

    @Provides
    @ApplicationScope
    internal fun providePostFilters(): PostFiltersHolder = PostFiltersHolder()

    @Provides
    @ApplicationScope
    internal fun provideAnalyticsFacade(app: Application): AnalyticsFacade {
        val modules = mutableListOf<AnalyticsModule>()

        if(BuildConfig.ANALYTICS_ENABLED) {
            modules.add(AmplitudeAnalyticsModule())

            if(BuildConfig.DEBUG) {
                modules.add(DebugAnalyticsModule())
            }
        }

        return AnalyticsFacadeImpl(app, modules)
    }
}