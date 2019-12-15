package com.galou.watchmyback.utils.rvAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.base.UserListBaseViewModel
import com.galou.watchmyback.data.applicationUse.OtherUser
import com.galou.watchmyback.databinding.FriendsRvItemBinding

/**
 * @author galou
 * 2019-11-05
 */
class UsersListAdapter(
    var users: List<OtherUser>,
    private val viewModel: UserListBaseViewModel
) : RecyclerView.Adapter<UsersViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
         return UsersViewHolder.from(
             parent
         )
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
        with(binding){
            friend = user
            viewmodel = viewModel
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): UsersViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = FriendsRvItemBinding.inflate(layoutInflater, parent, false)
            return UsersViewHolder(binding)
        }
    }
}