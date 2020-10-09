package io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.CommunityAllProposalsViewModel
import io.golos.cyber_android.ui.screens.post_view.dto.PostHeader
import io.golos.cyber_android.ui.shared.utils.toTimeEstimateFormat
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ProposalDomain
import kotlinx.android.synthetic.main.item_all_proposals.view.*
import java.text.SimpleDateFormat
import java.util.*

class AllProposalsAdapter(val items: List<ProposalDomain>, val viewModel: CommunityAllProposalsViewModel) : RecyclerView.Adapter<AllProposalsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_all_proposals, parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(proposalDomain: ProposalDomain) {
            with(itemView) {

                val blockTime: Date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss").parse(proposalDomain.blockTime)
                val expiration: Date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss").parse(proposalDomain.expiration)
                val exparingDate = expiration.toTimeEstimateFormat(context)
                val header =
                    PostHeader(proposalDomain.community?.name, proposalDomain.community?.avatarUrl, proposalDomain.community?.communityId, blockTime, proposalDomain.proposer?.username, proposalDomain.proposer?.userId?.userId
                        ?: "", proposalDomain.proposer?.avatarUrl, canJoinToCommunity = false, isBackFeatureEnabled = false, reward = null)
                postHeader.setHeader(header)
                postHeader.hideActionMenu()
                vApprove.text =
                    String.format("%s %s %s %s", proposalDomain.approvesCount, resources.getString(R.string.from), proposalDomain.approvesNeed, resources.getString(R.string.leaders))
                vBanExpiring.text =
                    String.format("%s%s %s%S", "(", resources.getString(R.string.exparing_in), exparingDate,")")

                postHeader.setOnUserClickListener {
                    viewModel.onUserInHeaderClick(proposalDomain.proposer?.userId?.userId!!)
                }
                postHeader.setOnCommunityClickListener {
                    viewModel.onCommunityClicked(proposalDomain.community?.communityId!!)
                }
                vBan.text=proposalDomain.type
            }
        }
    }
}