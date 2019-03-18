package io.golos.domain

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
class TestLogger : Logger {
    override fun invoke(e: Exception) {
        e.printStackTrace()
    }
}