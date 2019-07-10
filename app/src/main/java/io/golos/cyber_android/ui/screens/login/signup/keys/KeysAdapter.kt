package io.golos.cyber_android.ui.screens.login.signup.keys

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.item_key.view.*

class KeysAdapter(private val values: List<Key>): RecyclerView.Adapter<KeysAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_key, parent, false))
    }

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(key: Key) {
            with(itemView) {
                keyTitle.text = itemView.context.getString(key.title)
                keyValue.text = key.key
            }
        }
    }
}

private val Key.title: Int
    get() = when (this.keyType) {
        KeyType.MASTER -> R.string.master_password
        KeyType.POSTING -> R.string.posting_key
        KeyType.ACTIVE -> R.string.active_key
        KeyType.MEMO -> R.string.memo_key
    }