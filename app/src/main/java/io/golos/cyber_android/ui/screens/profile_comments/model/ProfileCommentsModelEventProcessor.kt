package io.golos.cyber_android.ui.screens.profile_comments.model

interface ProfileCommentsModelEventProcessor :
    ProfileCommentsProgressEventProcessor,
    ProfileCommentsVoteListener

interface ProfileCommentsProgressEventProcessor {
    fun onRetryLoadComments()
}

interface ProfileCommentsVoteListener {

    fun onCommentUpVoteClick(commentId: String)

    fun onCommentDownVoteClick(commentId: String)

}