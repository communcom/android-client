package io.golos.domain.entities

import io.golos.domain.Entity
import io.golos.domain.model.Identifiable
import java.lang.IllegalArgumentException

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
data class VoteRequestEntity(val power: Short, val discussionIdEntity: DiscussionIdEntity) : Entity, Identifiable {
    init {
        if (power > 10_000)throw IllegalArgumentException("vote range exceed, now $power")
        if (power < -10_000)throw IllegalArgumentException("vote range to low, now $power")
    }

    inner class Id(val discussionIdEntity: DiscussionIdEntity) : Identifiable.Id() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (discussionIdEntity != other.discussionIdEntity) return false

            return true
        }

        override fun hashCode(): Int {
            return discussionIdEntity.hashCode()
        }
    }

    override val id: Identifiable.Id
            by lazy { Id(discussionIdEntity) }
}