package io.golos.data.api.user

import io.golos.domain.entities.FollowerDomain
import io.golos.domain.entities.FollowersPageDomain
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class UsersApiImpl @Inject constructor() : UsersApi {


    override suspend fun getFollowers(query: String?, sequenceKey: String?, pageSizeLimit: Int): FollowersPageDomain {
        delay(2000)
        randomException()
        return getMockFollowersList()
    }

    private fun getMockFollowersList(): FollowersPageDomain {
        val followerFirstNamesList = mutableListOf<String>()
        followerFirstNamesList.add("Alexey")
        followerFirstNamesList.add("Sam")
        followerFirstNamesList.add("Sergey")
        followerFirstNamesList.add("Kostantin")
        followerFirstNamesList.add("Behance")

        val followerLastNamesList = mutableListOf<String>()
        followerFirstNamesList.add("Ivanov")
        followerFirstNamesList.add("Smith")
        followerFirstNamesList.add("Marchenko")
        followerFirstNamesList.add("Vahovskiy")
        followerFirstNamesList.add("Indigo")

        val avatarArray = mutableListOf<String>()
        avatarArray.add("https://images.fastcompany.net/image/upload/w_596,c_limit,q_auto:best,f_auto/fc/3034007-inline-i-applelogo.jpg")
        avatarArray.add("https://brandmark.io/logo-rank/random/beats.png")
        avatarArray.add("https://brandmark.io/logo-rank/random/pepsi.png")
        avatarArray.add("https://99designs-start-attachments.imgix.net/alchemy-pictures/2019%2F01%2F31%2F23%2F04%2F58%2Ff99d01d7-bf50-4b79-942f-605d6ed1fdce%2Fludibes.png?auto=format&ch=Width%2CDPR&w=250&h=250")

        val followersList = mutableListOf<FollowerDomain>()
        val rand = Random

        for (i in 0..30) {
            val firstName = followerFirstNamesList[rand.nextInt(followerFirstNamesList.size - 1)]
            val lastName = followerFirstNamesList[rand.nextInt(followerFirstNamesList.size - 1)]
            val avatar: String = avatarArray[rand.nextInt(avatarArray.size - 1)]

            val followerDomain = FollowerDomain(UUID.randomUUID().toString(), firstName, lastName, avatar, rand.nextBoolean())
            followersList.add(followerDomain)
        }
        return FollowersPageDomain(UUID.randomUUID().toString(), followersList)
    }

    private fun randomException() {
        val rand = Random
        if (rand.nextBoolean()) {
            throw RuntimeException()
        }
    }

    override suspend fun subscribeToFollower(userId: String) {

    }

    override suspend fun unsubscribeToFollower(userId: String) {

    }
}