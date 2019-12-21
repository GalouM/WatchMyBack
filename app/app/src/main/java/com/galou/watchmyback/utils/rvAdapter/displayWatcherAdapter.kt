package com.galou.watchmyback.utils.rvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.databinding.WatcherTripDetailsRvItemBinding

/**
 * @author galou
 * 2019-12-18
 */

class DisplayWatcherAdapter(
    val watchers: List<User>
) : RecyclerView.Adapter<DisplayWatcherViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayWatcherViewHolder =
        DisplayWatcherViewHolder.from(parent)

    override fun getItemCount(): Int = watchers.size

    override fun onBindViewHolder(holder: DisplayWatcherViewHolder, position: Int) {
        holder.bindWithWatcher(watchers[position])
    }
}

class DisplayWatcherViewHolder(private val binding: WatcherTripDetailsRvItemBinding) : RecyclerView.ViewHolder(binding.root){
    fun bindWithWatcher(tripWatcher: User){
        with(binding){
            watcher = tripWatcher
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): DisplayWatcherViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = WatcherTripDetailsRvItemBinding.inflate(layoutInflater, parent, false)
            return DisplayWatcherViewHolder(binding)
        }
    }
}