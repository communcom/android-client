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
                "title",
                "#sdg 1900-01-01 2007/08/13 1900.01.01 1900 01 01 " +
                        "1900-01.01 1900 13 01 1900 02 31 #ddfFh #ыEвавыа ывпыв ывпывыв ывпывпвыпывп пывпвпвы ыпып 1235235 " +
                        "ывп ывп ывп #ыв12sdg sg235#sg  sdg3E##see  #sd sdsg#sg23 sdfs##etwet #e etsset #ewtetw12#sdgsd sd#sdgsdg#sdgsdggs###sdg " +
                        "###sdggsgdg #тэгНарусском ###sgdgSDF   z#aaa#bbb #aaa#bbb  #ddfFh_32 #sdgsdsdgsdgsdgsdgsdg235325sdgsdgsdgdsg" +
                        "sdgsdg" +
                        "" +
                        "" +
                        "http://www.foufos.gr  https://www.foufos.gr  http://foufos.gr   http://www.foufos.gr/kino  http://werer.gr  www.foufos.gr   www.mp3.com www.t.co http://t.co http://www.t.co https://www.t.co www.aa.com http://aa.com http://www.aa.com https://www.aa.com www.foufos www.foufos-.gr www.-foufos.gr foufos.gr http://www.foufos http://foufos   www.mp3#.com https://vk.com/id235540857 https://www.linkedin.com/feed/\n" +
                        " //.com https://www.reddit.com/ https://www.reddit.com/r/aww/comments/b8sqoe/i_taught_him_gentle_at_a_young_age_he_takes_it/\n" +
                        "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789 _+-.,!@#\$%^&*();\\/|<>\"' 12345 -98.7 3.141 .6180 9,000 +42 555.123.4567    +1-(800)-555-2468 foo@demo.net    bar.ba@test.co.uk www.demo.com    http://foo.co.uk/fo http://regexr.com/foo.html?q=bar https://mediatemple.net/fo",
                listOf("nsfw")
            )
        )

        assertEquals(
            listOf("nsfw", "sdg", "ddfFh", "sd", "aaa", "ewtetw12", "ddfFh_32").sorted(),
            (out as CreatePostRequest).tags.map { it.tag }.sorted()
        )

        assertEquals(
            listOf(
                "http://www.foufos.gr",
                "https://www.foufos.gr",
                "http://foufos.gr",
                "http://www.foufos.gr/kino",
                "http://werer.gr",
                "www.foufos.gr",
                "www.mp3.com",
                "www.t.co",
                "http://t.co",
                "http://www.t.co",
                "https://www.t.co",
                "www.aa.com",
                "http://aa.com",
                "http://www.aa.com",
                "https://www.aa.com",
                "https://vk.com/id235540857",
                "https://www.linkedin.com/feed/",
                "https://www.reddit.com/",
                "https://www.reddit.com/r/aww/comments/b8sqoe/i_taught_him_gentle_at_a_young_age_he_takes_it/",
                "www.demo.com",
                "http://foo.co.uk/fo",
                "http://regexr.com/foo.html?q=bar",
                "https://mediatemple.net/fo"
            ).sorted(),
            out.metadata.embeds.map { it.url }.sorted())
    }
}
