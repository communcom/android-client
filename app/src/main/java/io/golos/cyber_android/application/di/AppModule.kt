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
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacade
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacadeImpl
import io.golos.cyber_android.application.shared.analytics.modules.AnalyticsModule
import io.golos.cyber_android.application.shared.analytics.modules.amplitude.AmplitudeAnalyticsModule
import io.golos.cyber_android.application.shared.analytics.modules.debug.DebugAnalyticsModule
import io.golos.cyber_android.application.shared.logger.CrashlyticsTimberTreeDebug
import io.golos.cyber_android.application.shared.logger.CrashlyticsTimberTreeRelease
import io.golos.cyber_android.application.shared.logger.Cyber4JLogger
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.data.encryption.aes.EncryptorAES
import io.golos.data.encryption.aes.EncryptorAESOldApi
import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Named

/** Application level module - global objects are created here   */
@Module
class AppModule(
    private val app: Application
) {
    private val cyber4jConfigs = mapOf(
        "dev" to Commun4jConfig(
            blockChainHttpApiUrl = "http://116.202.4.46:8888/",
            servicesUrl = "wss://dev-gate.commun.com",
            socketOpenQueryParams = SocketOpenQueryParams(
                version = BuildConfig.VERSION_NAME,
                deviceType = GlobalConstants.DEVICE_TYPE,
                platform = GlobalConstants.PLATFORM,
                clientType = GlobalConstants.CLIENT_TYPE )
        ),
        "prod" to Commun4jConfig(
            blockChainHttpApiUrl = "https://node.commun.com/",
            servicesUrl = "wss://gate.commun.com/",
            socketOpenQueryParams = SocketOpenQueryParams(
                version = BuildConfig.VERSION_NAME,
                deviceType = GlobalConstants.DEVICE_TYPE,
                platform = GlobalConstants.PLATFORM,
                clientType = GlobalConstants.CLIENT_TYPE )
        )
    )

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
    internal fun provideConfig(): Commun4jConfig =
        (cyber4jConfigs[BuildConfig.FLAVOR])!!
            .copy(
                httpLogger = Cyber4JLogger(Cyber4JLogger.HTTP),
                socketLogger = Cyber4JLogger(Cyber4JLogger.SOCKET)
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
        if(BuildConfig.DEBUG) {
            CrashlyticsTimberTreeDebug()
        } else {
            CrashlyticsTimberTreeRelease(crashlytics)
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