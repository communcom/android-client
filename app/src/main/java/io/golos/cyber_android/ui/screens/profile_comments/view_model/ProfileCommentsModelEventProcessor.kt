package io.golos.cyber_android.ui.screens.profile_comments.view_model

import io.golos.cyber_android.ui.shared.widgets.post_comments.*
import io.golos.cyber_android.ui.dto.Comment
import io.golos.cyber_android.ui.dto.DonateType
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.DonationsDomain
import io.golos.domain.dto.UserBriefDomain

interface ProfileCommentsModelEventProcessor :
    ProfileCommentsProgressEventProcessor,
    ProfileCommentsVoteListener,
    RichWidgetListener,
    EmbedWidgetListener,
    AttachmentWidgetListener,
    EmbedImageWidgetListener,
    EmbedWebsiteWidgetListener,
    EmbedVideoWidgetListener,
    ParagraphWidgetListener,
    CommentLongClickListener,
    CommentClickListener,
    DonateClickListener

interface ProfileCommentsProgressEventProcessor {
    fun onRetryLoadComments()
}

interface ProfileCommentsVoteListener {
    fun onCommentUpVoteClick(commentId: ContentIdDomain)

    fun onCommentDownVoteClick(commentId: ContentIdDomain)

    fun onDonateClick(donate: DonateType, contentId: ContentIdDomain, communityId: CommunityIdDomain, contentAuthor: UserBriefDomain)
}

interface CommentLongClickListener {
    fun onCommentLongClick(comment: Comment)
}

interface CommentClickListener{
    fun onCommentClicked(comment: Comment)
}

interface DonateClickListener{
    fun onDonatePopupClick(donates: DonationsDomain)
}