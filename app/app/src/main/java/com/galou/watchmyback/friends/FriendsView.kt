package com.galou.watchmyback.friends


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.databinding.FragmentFriendsViewBinding
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
    private lateinit var adapterRv: FriendsAdapter

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
        adapterRv = FriendsAdapter(listOf(), viewModel)
        recyclerView.adapter = adapterRv

    }

    private fun setupObserverViewModel(){
        setupFriendsList()
        setupSnackBar()

    }

    private fun setupFriendsList(){
        viewModel.friendsLD.observe(this, Observer { updateFriendsList(it) })
    }

    private fun setupSnackBar(){
        val rooView = binding.friendsViewContainer
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun updateFriendsList(friends: List<OtherUser>){
        adapterRv.friends = friends
        adapterRv.notifyDataSetChanged()
    }


}
