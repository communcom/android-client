package io.golos.cyber_android.services.post_view

import android.app.IntentService
import android.content.Context
import android.content.Intent
import io.golos.cyber_android.application.App
import io.golos.cyber_android.services.post_view.di.RecordPostViewServiceComponent
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.repositories.DiscussionRepository
import io.golos.utils.id.IdUtil
import javax.inject.Inject
import javax.inject.Named

class RecordPostViewService : IntentService("RecordPostViewService") {
    companion object {
        private const val ACTION_RECORD = "io.golos.cyber_android.services.post_view.action.ACTION_RECORD"
        private const val POST_ID = "io.golos.cyber_android.services.post_view.action.POST_ID"

        @JvmStatic
        fun record(context: Context, postId: ContentIdDomain) {
            val intent = Intent(context, RecordPostViewService::class.java).apply {
                action = ACTION_RECORD
                putExtra(POST_ID, postId)
            }
            context.startService(intent)
        }
    }

    @Inject
    internal lateinit var discussionRepository: DiscussionRepository

    @Inject
    @field:Named(Clarification.DEVICE_ID)
    internal lateinit var deviceId: String

    private lateinit var injectionKey: String

    override fun onCreate() {
        super.onCreate()

        injectionKey = IdUtil.generateStringId()
        App.injections.get<RecordPostViewServiceComponent>(injectionKey).inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<RecordPostViewServiceComponent>(injectionKey)
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_RECORD -> handleActionRecord(intent.getParcelableExtra(POST_ID) as ContentIdDomain)
        }
    }

    private fun handleActionRecord(postId: ContentIdDomain) {
        discussionRepository.recordPostView(postId, deviceId)
    }
}
