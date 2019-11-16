package com.galou.watchmyback.friends


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.EventObserver
import com.galou.watchmyback.R
import com.galou.watchmyback.addFriend.AddFriendActivity
import com.galou.watchmyback.base.UsersListAdapter
import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.databinding.FragmentFriendsViewBinding
import com.galou.watchmyback.utils.RC_ADD_FRIEND
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class FriendsView : Fragment() {

    private val viewModel: FriendsViewModel by viewModel()
    private lateinit var binding: FragmentFriendsViewBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRv: UsersListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        configureBinding(inflater, container)
        configureRecyclerView()
        setupObserverViewModel()

        return binding.root
    }

    private fun configureBinding(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends_view, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
    }

    private fun configureRecyclerView(){
        recyclerView = binding.friendsViewRv
        adapterRv = UsersListAdapter(listOf(), viewModel)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapterRv

    }

    private fun setupObserverViewModel(){
        setupFriendsList()
        setupSnackBar()
        setupOpenAddFriend()

    }

    private fun setupFriendsList(){
        viewModel.usersLD.observe(this, Observer { updateFriendsList(it) })
    }

    private fun setupSnackBar(){
        val rooView = binding.friendsViewContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun setupOpenAddFriend(){
        viewModel.openAddFriendLD.observe(this, EventObserver { openAddFriendActivity() })
    }

    private fun updateFriendsList(friends: List<OtherUser>){
        adapterRv.update(friends)
    }

    private fun openAddFriendActivity(){
        val intent = Intent(activity!!, AddFriendActivity::class.java)
        startActivityForResult(intent, RC_ADD_FRIEND)
    }


}
