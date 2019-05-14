package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.cyber_android.countriesRepo
import io.golos.cyber_android.dispatchersProvider
import io.golos.cyber_android.toCountryModelMapper
import io.golos.domain.interactors.reg.CountriesChooserUseCase
import io.golos.domain.requestmodel.QueryResult
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
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

    @Test
    fun flagsCreation() {
        val moshi = Moshi.Builder().build()

        val codesString = this::class.java.getResource("/posts.json").readText()
        Assert.assertNotNull(codesString)


        val phoneCodesList = moshi.adapter<List<CountryPhoneCode>>(
            Types.newParameterizedType(
                List::class.java,
                CountryPhoneCode::class.java
            )
        )
            .fromJson(codesString)!!

        val countryCodesString = this::class.java.getResource("/codes.json").readText()
        Assert.assertNotNull(countryCodesString)

        val countryCodes =
            moshi.adapter<List<CountryCodes>>(Types.newParameterizedType(List::class.java, CountryCodes::class.java))
                .fromJson(countryCodesString)!!

        val out = phoneCodesList.map { countryWithPhoneCode ->
            val countryWithCountryCode = countryCodes.find { it.name == countryWithPhoneCode.label }
            if (countryWithCountryCode == null)
                println("cannot find country with name ${countryWithPhoneCode.label}")
            Country(
                countryWithPhoneCode.code,
                countryWithCountryCode!!.code,
                countryWithPhoneCode.label,
                "http://www.geognos.com/api/en/countries/flag/${countryWithCountryCode.code}.png"
            )
        }

        val outString = moshi.adapter<List<Country>>(
            Types.newParameterizedType(
                List::class.java,
                Country::class.java
            )
        ).toJson(out)

        println(outString)
    }
}


class CountryPhoneCode(
    @Json(name = "code")
    val code: Int,
    val label: String
) {
    override fun toString(): String {
        return "CountryPhoneCode(countryCode=$code, label='$label')"
    }
}

class CountryCodes(
    val name: String,
    val code: String
) {
    override fun toString(): String {
        return "CountryCodes(name='$name', countryCode='$code')"
    }
}

class Country(
    val countryPhoneCode: Int,
    val countryCode: String,
    val countryName: String,
    val thumbNailUrl: String //size 64*64
)