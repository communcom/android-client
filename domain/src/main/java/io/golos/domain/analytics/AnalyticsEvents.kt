package io.golos.domain.analytics

interface AnalyticsEvents {
    /**
     * Event: Click get started 0.3.1
     */
    fun clickGetStarted031()

    /**
     * Event: Open screen 0.1.1
     */
    fun openScreen011()

    /**
     * Event: Open screen 0.1.2
     */
    fun openScreen012()

    /**
     * Event: Open screen 0.1.3
     */
    fun openScreen013()

    /**
     * Event: Click log in 0.1
     */
    fun clickLogIn01()

    /**
     * Event: Registration Selection
     */
    fun registrationSelection(param: RegistrationSelection)

    /**
     * Event: Country selected
     * [available] true - the country is accessible; false - a user tried to select disabled country
     * [code] phone code
     */
    fun countrySelected(available: Boolean, code: String)

    /**
     * Event: Phone number entered
     */
    fun phoneNumberEntered()

    /**
     * Event: Sms code entered
     */
    fun smsCodeEntered(param: SmsCodeEntered)

    /**
     * Event: Username entered
     */
    fun usernameEntered(available: UsernameEntered)

    /**
     * Event: Password copy
     */
    fun passwordCopy()

    /**
     * Event: Password backuped
     */
    fun passwordBackuped(param: PasswordBackup)

    /**
     * Event: Password not backuped
     * [param] true - an extra notification about saving a password has been skipped;
     * false - the user has returned to saving a password
     */
    fun passwordNotBackuped(param: Boolean)

    /**
     * Event: FaceID/TouchID activated
     * [param] true - was activated; false - wasn't activated
     */
    fun faceIDTouchIDActivated(param: Boolean)

    /**
     * Event: Open screen 1.
     */
    fun openScreen1()

    /**
     * Event: Open screen 1.1.0
     */
    fun openScreen110()

    /**
     * Event: Open screen 1.1.1
     */
    fun openScreen111()

    /**
     * Event: Open screen 1.1.2
     */
    fun openScreen112()

    /**
     * Event: Open screen 1.1.3
     */
    fun openScreen113()

    /**
     * Event: Open screen 1.1.4
     */
    fun openScreen114()

    /**
     * Event: Open screen 1.1.5
     */
    fun openScreen115()

    /**
     * Event: Open screen 1.1.6
     */
    fun openScreen116()

    /**
     * Event: Google auth
     * [param] true - access to Google has been granted; false - otherwise
     */
    fun googleAuth(param: Boolean)

    /**
     * Event: Facebook auth
     * [param] true - access to Facebook has been granted; false - otherwise
     */
    fun facebookAuth(param: Boolean)

    /**
     * Event: Apple auth
     * [param] true - access to the Apple has been granted; false - otherwise
     */
    fun appleAuth(param: Boolean)

    /**
     * Event: Open screen 1.2.1
     */
    fun openScreen121()

    /**
     * Event: Open screen 1.3.1
     */
    fun openScreen131()

    /**
     * Event: Open screen 1.4.1
     */
    fun openScreen141()

    /**
     * Event: Bounty subscribe
     * [num] subscriptions quantity
     */
    fun bountySubscribe(num: Int)

    /**
     * Event: Share activated
     * [param] true - if a user shared a link; false - otherwise
     */
    fun shareActivated(param: Boolean)

    /**
     * Event: app was download
     * [param] true - the app has been downloaded; false - otherwise
     */
    fun appWasDownloaded(param: Boolean)

    /**
     * Event: Share activated referal
     * [param] true - if a user shared a link; false - otherwise
     */
    fun shareActivatedReferal(param: Boolean)
}