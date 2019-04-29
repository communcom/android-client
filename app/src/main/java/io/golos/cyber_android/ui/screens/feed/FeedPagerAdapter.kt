package io.golos.cyber_android.ui.screens.feed

//class FeedPagerAdapter(private val updatesRequestCallBack: ((Tab) -> Unit),
//    private val listener: PostsAdapter.Listener
//): RecyclerView.Adapter<FeedPagerAdapter.NotificationViewHolder>() {
//
//    enum class Tab(@StringRes val title: Int, val index: Int) {
//        ALL(R.string.tab_all, 0), MY_FEED(R.string.tab_my_feed, 1)
//    }
//
//    private val holders = mutableMapOf<Tab, NotificationViewHolder>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
//        val recyclerView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_feed_pager, parent, false) as RecyclerView
//        recyclerView.layoutManager = LinearLayoutManager(parent.context, RecyclerView.VERTICAL, false)
//        recyclerView.addItemDecoration(DividerItemDecoration(parent.context, DividerItemDecoration.VERTICAL).apply {
//            setDrawable(ContextCompat.getDrawable(parent.context, R.drawable.divider_post_card)!!)
//        })
//        return NotificationViewHolder(recyclerView)
//    }
//
//    override fun getItemCount() = Tab.values().size
//
//    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
//        if (position == Tab.ALL.index) {
//            holder.recyclerView.adapter = PostsAdapter(PostsDiffCallback(), listener)
//            holders[Tab.ALL] = holder
//            updatesRequestCallBack.invoke(Tab.ALL)
//            restoreCallback?.invoke()
//            restoreCallback = null
//        }
//    }
//
//    fun submitAllList(list: PagedList<PostModel>) {
//        holders[Tab.ALL]?.let {
//            (it.recyclerView.adapter as PostsAdapter).submitList(list)
//        }
//    }
//
//    var restoreCallback : (() -> Unit)? = null
//
//    fun saveState(outState: Bundle) {
//        holders[Tab.ALL]?.let {
//            if (it.recyclerView.layoutManager != null)
//                outState.putParcelable(Tab.ALL.name + javaClass.name, it.recyclerView.layoutManager?.onSaveInstanceState())
//        }
//    }
//
//    fun restoreState(savedInstanceState: Bundle) {
//        restoreCallback = {
//            holders[Tab.ALL]?.let {
//                val savedRecyclerLayoutState = savedInstanceState.getParcelable(Tab.ALL.name + javaClass.name) as Parcelable
//                it.recyclerView.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
//            }
//        }
//    }
//
//    class NotificationViewHolder(val recyclerView: RecyclerView): RecyclerView.NotificationViewHolder(recyclerView)
//
//}