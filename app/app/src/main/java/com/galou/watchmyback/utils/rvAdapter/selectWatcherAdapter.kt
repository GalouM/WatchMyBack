package com.galou.watchmyback.utils.rvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.Watcher
import com.galou.watchmyback.databinding.SelectWatcherItemBinding

/**
 * @author galou
 * 2019-12-03
 */

class SelectWatcherAdapter(
    var watchers: List<Watcher>,
    private val listener: WatcherListener
) : RecyclerView.Adapter<SelectWatcherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectWatcherViewHolder =
        SelectWatcherViewHolder.from(parent)

    override fun getItemCount(): Int = watchers.size

    override fun onBindViewHolder(holder: SelectWatcherViewHolder, position: Int) {
        holder.bindWithWatcher(listener, watchers[position])
    }
}

class SelectWatcherViewHolder(private val binding: SelectWatcherItemBinding) : RecyclerView.ViewHolder(binding.root){

    fun bindWithWatcher(watcherListener: WatcherListener, watcher: Watcher){
        with(binding){
            this.watcher = watcher
            listener = watcherListener
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): SelectWatcherViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SelectWatcherItemBinding.inflate(layoutInflater, parent, false)
            return SelectWatcherViewHolder(binding)
        }
    }
}