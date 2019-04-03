package io.golos.domain.rules

import io.golos.domain.model.CreatePostRequest
import io.golos.domain.model.PostCreationRequestEntity
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-03.
 */
class RequestEntityToArgumentsMapperTest {

    private val mapper = RequestEntityToArgumentsMapper()

    @Test
    fun testHashTagExtractor() = runBlocking {
        val out = mapper(
            PostCreationRequestEntity(
                "title", "#sdg 1900-01-01 2007/08/13 1900.01.01 1900 01 01 " +
                        "1900-01.01 1900 13 01 1900 02 31 #ddfFh #ыEвавыа ывпыв ывпывыв ывпывпвыпывп пывпвпвы ыпып 1235235 " +
                        "ывп ывп ывп #ыв12sdg sg235#sg  sdg3E##see  #sd sdsg#sg23 sdfs##etwet #e etsset #ewtetw12#sdgsd sd#sdgsdg#sdgsdggs###sdg " +
                        "###sdggsgdg #тэгНарусском ###sgdgSDF   z#aaa#bbb #aaa#bbb  #ddfFh_32 #sdgsdsdgsdgsdgsdgsdg235325sdgsdgsdgdsg", listOf("nsfw")
            )
        )

        assertEquals(listOf("nsfw", "sdg", "ddfFh", "sd", "aaa",  "ewtetw12", "ddfFh_32").sorted(),
            (out as CreatePostRequest).tags.map { it.tag }.sorted())
    }
}