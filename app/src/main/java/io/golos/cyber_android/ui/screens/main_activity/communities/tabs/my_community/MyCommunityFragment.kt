package io.golos.cyber_android.ui.screens.main_activity.communities.tabs.my_community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.my_community_fragment.MyCommunityFragmentComponent
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.ChildSearchFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.search_bridge.SearchBridgeChild
import javax.inject.Inject

class MyCommunityFragment : Fragment(), ChildSearchFragment {
    companion object {
        fun newInstance() = MyCommunityFragment()
    }

    @Inject
    internal lateinit var searchBridge: SearchBridgeChild

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<MyCommunityFragmentComponent>().inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.injections.release<MyCommunityFragmentComponent>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_my_communities, container, false)

    override fun onResume() {
        super.onResume()
        searchBridge.getParent().setSearchString(searchString)
    }

    private var searchString = ""               // DEBUG!!!!!!!!!!!!!!!!!!!!!!
    override fun onSearchStringUpdate(searchString: String) {
        this.searchString = searchString
    }
}
