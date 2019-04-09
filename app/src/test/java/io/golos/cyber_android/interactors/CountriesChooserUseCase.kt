package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.countriesRepo
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.toCountryModelMapper
import io.golos.domain.interactors.reg.CountriesChooserUseCase
import io.golos.domain.model.QueryResult
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
class CountriesChooserUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    lateinit var useCase: CountriesChooserUseCase

    @Before
    fun before() {
        useCase = CountriesChooserUseCase(
            countriesRepo,
            toCountryModelMapper,
            dispatchersProvider
        )
    }

    @Test
    fun filterTest() = runBlocking {

        useCase.subscribe()
        useCase.unsubscribe()
        useCase.subscribe()

        var loadingState = useCase.useCaseRedinessState.value
        useCase.useCaseRedinessState.observeForever {
            loadingState = it
        }

        while (loadingState !is QueryResult.Success) delay(100)

        var result = useCase.getAsLiveData.value

        useCase.getAsLiveData.observeForever {
            result = it
        }

        useCase.makeSearch(7)

        delay(100)

        assertTrue(!result!!.isEmpty())


        useCase.makeSearch("ru")

        assertTrue(!result!!.isEmpty())
        assertTrue(result!!.any { it.countryPhoneCode == 7 })
    }
}