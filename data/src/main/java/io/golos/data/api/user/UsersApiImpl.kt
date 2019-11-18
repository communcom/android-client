package io.golos.data.api.user

import io.golos.domain.dto.FollowerDomain
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class UsersApiImpl @Inject constructor() : UsersApi {


    override suspend fun getFollowers(query: String?, offset: Int, pageSizeLimit: Int): List<FollowerDomain> {
        delay(2000)
        //randomException()
        return getMockFollowersList()
    }

    private fun getMockFollowersList(): List<FollowerDomain> {
        val followerFirstNamesList = mutableListOf<String>()
        followerFirstNamesList.add("Alexey")
        followerFirstNamesList.add("Sam")
        followerFirstNamesList.add("Sergey")
        followerFirstNamesList.add("Kostantin")
        followerFirstNamesList.add("Behance")

        val followerLastNamesList = mutableListOf<String>()
        followerLastNamesList.add("Ivanov")
        followerLastNamesList.add("Smith")
        followerLastNamesList.add("Marchenko")
        followerLastNamesList.add("Vahovskiy")
        followerLastNamesList.add("Indigo")

        val avatarArray = mutableListOf<String>()
        avatarArray.add("https://images.fastcompany.net/image/upload/w_596,c_limit,q_auto:best,f_auto/fc/3034007-inline-i-applelogo.jpg")
        avatarArray.add("https://brandmark.io/logo-rank/random/beats.png")
        avatarArray.add("https://brandmark.io/logo-rank/random/pepsi.png")
        avatarArray.add("https://99designs-start-attachments.imgix.net/alchemy-pictures/2019%2F01%2F31%2F23%2F04%2F58%2Ff99d01d7-bf50-4b79-942f-605d6ed1fdce%2Fludibes.png?auto=format&ch=Width%2CDPR&w=250&h=250")

        val followersList = mutableListOf<FollowerDomain>()
        val rand = Random

        for (i in 0..30) {
            val firstName = followerFirstNamesList[rand.nextInt(followerFirstNamesList.size - 1)]
            val lastName = followerLastNamesList[rand.nextInt(followerLastNamesList.size - 1)]
            val avatar: String = avatarArray[rand.nextInt(avatarArray.size - 1)]

            val followerDomain = FollowerDomain(UUID.randomUUID().toString(), firstName, lastName, avatar, rand.nextBoolean())
            followersList.add(followerDomain)
        }
        return followersList
    }

    private fun randomException() {
        val rand = Random
        if (rand.nextBoolean()) {
            throw RuntimeException()
        }
    }

    override suspend fun subscribeToFollower(userId: String) {
        delay(2000)
        randomException()
    }

    override suspend fun unsubscribeToFollower(userId: String) {
        delay(2000)
        randomException()
    }
}