package io.golos.cyber_android.ui.screens.community_page_rules

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityPageRulesBinding
import io.golos.cyber_android.ui.screens.community_page_rules.di.CommunityPageRulesFragmentComponent
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.domain.dto.CommunityRuleDomain
import kotlinx.android.synthetic.main.fragment_community_page_rules.*

class CommunityPageRulesFragment :
    FragmentBaseMVVM<FragmentCommunityPageRulesBinding, CommunityPageRulesViewModel>() {

    override fun provideViewModelType(): Class<CommunityPageRulesViewModel> = CommunityPageRulesViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_page_rules

    override fun inject(key: String) = App.injections
        .get<CommunityPageRulesFragmentComponent>(key, getRules())
        .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<CommunityPageRulesFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentCommunityPageRulesBinding, viewModel: CommunityPageRulesViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.communityPageRulesLiveData.observe({ viewLifecycleOwner.lifecycle }) {
            // List<CommunityRuleDomain>
//            if (TextUtils.isEmpty(it)) {
                tvRules.text = getString(R.string.missing_description)
//            } else {
//                tvRules.text = it
//            }
        }
        viewModel.start()
    }

    private fun getRules(): List<CommunityRuleDomain> {
        return arguments!!.getParcelableArray(ARG_RULES)!!.map { it as CommunityRuleDomain } .toList()
    }

    companion object {

        private const val ARG_RULES = "ARG_RULES"

        fun newInstance(description: List<CommunityRuleDomain>): Fragment {
            val bundle = Bundle()
            bundle.putParcelableArray(ARG_RULES, description.toTypedArray())
            val communityPageRulesFragment = CommunityPageRulesFragment()
            communityPageRulesFragment.arguments = bundle
            return communityPageRulesFragment
        }
    }
}