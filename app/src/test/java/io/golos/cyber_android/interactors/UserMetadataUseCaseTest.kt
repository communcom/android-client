package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.userMetadataRepos
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.requestmodel.QueryResult
import io.golos.sharedmodel.CyberName
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
class UserMetadataUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    lateinit var case: UserMetadataUseCase

    @Before
    fun before() {
        case = UserMetadataUseCase(CyberName("destroyer2k@golos"), userMetadataRepos)
    }

    @Test
    fun testFetchUser() {
        case.subscribe()
        case.unsubscribe()
        case.subscribe()
        var user = case.getAsLiveData.value
        case.getAsLiveData.observeForever {
            user = it
        }
        assertTrue(user is QueryResult.Success)
        println(user)
    }
}