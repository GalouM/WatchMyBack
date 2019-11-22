package com.galou.watchmyback.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.databinding.FriendsRvItemBinding
import com.galou.watchmyback.utils.displayData

/**
 * @author galou
 * 2019-11-05
 */
class UsersListAdapter(
    var users: List<OtherUser>,
    private val viewModel: UserListBaseViewModel
) : RecyclerView.Adapter<UsersViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
         return UsersViewHolder.from(parent)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bindWithUser(viewModel, users[position])
    }

    fun update(users: List<OtherUser>){
        this.users = users
        notifyDataSetChanged()
    }
}

class UsersViewHolder(private val binding: FriendsRvItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindWithUser(viewModel: UserListBaseViewModel, user: OtherUser){
        binding.friend = user
        binding.viewmodel = viewModel
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): UsersViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = FriendsRvItemBinding.inflate(layoutInflater, parent, false)
            return UsersViewHolder(binding)
        }
    }
}