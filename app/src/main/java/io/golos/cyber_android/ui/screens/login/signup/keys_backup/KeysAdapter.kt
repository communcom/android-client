package io.golos.cyber_android.ui.screens.login.signup.keys_backup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import kotlinx.android.synthetic.main.item_key.view.*
import java.lang.UnsupportedOperationException

class KeysAdapter(private val values: List<UserKey>): RecyclerView.Adapter<KeysAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_key, parent, false))
    }

    override fun getItemCount() = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(key: UserKey) {
            with(itemView) {
                keyTitle.text = itemView.context.getString(key.title)
                keyValue.text = key.key
            }
        }
    }
}

private val UserKey.title: Int
    get() = when (this.keyType) {
        UserKeyType.MASTER -> R.string.master_password
        UserKeyType.POSTING -> R.string.posting_key
        UserKeyType.ACTIVE -> R.string.active_key
        UserKeyType.MEMO -> R.string.memo_key
        UserKeyType.OWNER -> R.string.owner_key
        else -> throw UnsupportedOperationException("This type of key is not supported: ${this.keyType}")
    }