package com.galou.watchmyback.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.databinding.FriendsRvItemBinding

/**
 * @author galou
 * 2019-11-05
 */
class FriendsAdapter(
    var friends: List<OtherUser>,
    private val viewModel: FriendsViewModel
) : RecyclerView.Adapter<FriendViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
         return FriendViewHolder.from(parent)
    }

    override fun getItemCount(): Int = friends.size

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bindWithFriend(viewModel, friends[position])
    }
}

class FriendViewHolder(private val binding: FriendsRvItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindWithFriend(viewModel: FriendsViewModel, friend: OtherUser){
        binding.friend = friend
        binding.viewmodel = viewModel

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): FriendViewHolder{
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = FriendsRvItemBinding.inflate(layoutInflater, parent, false)
            return FriendViewHolder(binding)
        }
    }
}