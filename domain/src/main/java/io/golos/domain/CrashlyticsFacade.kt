package io.golos.domain

interface CrashlyticsFacade {
    /** */
    fun registerUser(userName: String, userId: String)

    /** */
    fun log(tag: String, string: String)

    /** */
    fun log(ex: Throwable)

    /** */
    fun sendReport(text: String)
}