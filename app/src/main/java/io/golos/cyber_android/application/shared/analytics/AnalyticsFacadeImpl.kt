package io.golos.cyber_android.application.shared.analytics

import android.app.Application
import io.golos.cyber_android.application.shared.analytics.dto.PasswordBackup
import io.golos.cyber_android.application.shared.analytics.dto.RegistrationSelection
import io.golos.cyber_android.application.shared.analytics.dto.SmsCodeEntered
import io.golos.cyber_android.application.shared.analytics.dto.UsernameEntered
import io.golos.cyber_android.application.shared.analytics.modules.AnalyticsModule
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class AnalyticsFacadeImpl
@Inject
constructor(
    private val app: Application,
    private val modules: List<AnalyticsModule>
) : AnalyticsFacade {

    override fun init() = modules.forEach { it.init(app) }

    /**
     * Event: Click get started 0.3.1
     */
    override fun clickGetStarted031() = modules.forEach { it.clickGetStarted031() }

    /**
     * Event: Open screen 0.1.1
     */
    override fun openScreen011() = modules.forEach { it.openScreen011() }

    /**
     * Event: Open screen 0.1.2
     */
    override fun openScreen012() = modules.forEach { it.openScreen012() }

    /**
     * Event: Open screen 0.1.3
     */
    override fun openScreen013() = modules.forEach { it.openScreen013() }

    /**
     * Event: Registration Selection
     */
    override fun registrationSelection(param: RegistrationSelection) = modules.forEach { it.registrationSelection(param) }

    /**
     * Event: Country selected
     * [available] true - the country is accessible; false - a user tried to select disabled country
     * [code] phone code
     */
    override fun countrySelected(available: Boolean, code: String) = modules.forEach { it.countrySelected(available, code) }

    /**
     * Event: Phone number entered
     */
    override fun phoneNumberEntered() = modules.forEach { it.phoneNumberEntered() }

    /**
     * Event: Sms code entered
     */
    override fun smsCodeEntered(param: SmsCodeEntered) = modules.forEach { it.smsCodeEntered(param) }

    /**
     * Event: Username entered
     */
    override fun usernameEntered(available: UsernameEntered) = modules.forEach { it.usernameEntered(available) }

    /**
     * Event: Password copy
     */
    override fun passwordCopy() = modules.forEach { it.passwordCopy() }

    /**
     * Event: Password backuped
     */
    override fun passwordBackuped(param: PasswordBackup) = modules.forEach { it.passwordBackuped(param) }

    /**
     * Event: Password not backuped
     * [param] true - an extra notification about saving a password has been skipped;
     * false - the user has returned to saving a password
     */
    override fun passwordNotBackuped(param: Boolean) = modules.forEach { it.passwordNotBackuped(param) }

    /**
     * Event: FaceID/TouchID activated
     * [param] true - was activated; false - wasn't activated
     */
    override fun faceIDTouchIDActivated(param: Boolean) = modules.forEach { it.faceIDTouchIDActivated(param) }

    /**
     * Event: Open screen 1.
     */
    override fun openScreen1() = modules.forEach { it.openScreen1() }

    /**
     * Event: Open screen 1.1.1
     */
    override fun openScreen111() = modules.forEach { it.openScreen111() }

    /**
     * Event: Open screen 1.1.2
     */
    override fun openScreen112() = modules.forEach { it.openScreen112() }

    /**
     * Event: Open screen 1.1.3
     */
    override fun openScreen113() = modules.forEach { it.openScreen113() }

    /**
     * Event: Open screen 1.1.4
     */
    override fun openScreen114() = modules.forEach { it.openScreen114() }

    /**
     * Event: Open screen 1.1.5
     */
    override fun openScreen115() = modules.forEach { it.openScreen115() }

    /**
     * Event: Open screen 1.1.6
     */
    override fun openScreen116() = modules.forEach { it.openScreen116() }

    /**
     * Event: Google auth
     * [param] true - access to Google has been granted; false - otherwise
     */
    override fun googleAuth(param: Boolean) = modules.forEach { it.googleAuth(param) }

    /**
     * Event: Facebook auth
     * [param] true - access to Facebook has been granted; false - otherwise
     */
    override fun facebookAuth(param: Boolean) = modules.forEach { it.facebookAuth(param) }

    /**
     * Event: Apple auth
     * [param] true - access to the Apple has been granted; false - otherwise
     */
    override fun appleAuth(param: Boolean) = modules.forEach { it.appleAuth(param) }

    /**
     * Event: Open screen 1.2.1
     */
    override fun openScreen121() = modules.forEach { it.openScreen121() }

    /**
     * Event: Open screen 1.3.1
     */
    override fun openScreen131() = modules.forEach { it.openScreen131() }

    /**
     * Event: Open screen 1.4.1
     */
    override fun openScreen141() = modules.forEach { it.openScreen141() }

    /**
     * Event: Bounty subscribe
     * [num] subscriptions quantity
     */
    override fun bountySubscribe(num: Int) = modules.forEach { it.bountySubscribe(num) }

    /**
     * Event: Share activated
     * [param] true - if a user shared a link; false - otherwise
     */
    override fun shareActivated(param: Boolean) = modules.forEach { it.shareActivated(param) }

    /**
     * Event: app was download
     * [param] true - the app has been downloaded; false - otherwise
     */
    override fun appWasDownloaded(param: Boolean) = modules.forEach { it.appWasDownloaded(param) }

    /**
     * Event: Share activated referal
     * [param] true - if a user shared a link; false - otherwise
     */
    override fun shareActivatedReferal(param: Boolean) = modules.forEach { it.shareActivatedReferal(param) }
}