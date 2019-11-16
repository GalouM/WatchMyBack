package com.galou.watchmyback.addFriend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.R
import com.galou.watchmyback.databinding.ActivityAddFriendBinding
import com.galou.watchmyback.base.UsersListAdapter
import com.galou.watchmyback.data.entity.OtherUser
import com.galou.watchmyback.utils.displayData
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class AddFriendActivity : AppCompatActivity() {

    private val viewModel: AddFriendViewModel by viewModel()
    private lateinit var binding: ActivityAddFriendBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterRv: UsersListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureBinding()
        configureToolbar()
        configureRecyclerView()
        setupObserverViewModel()
        configureEditTextListener()
    }

    //-------------------------
    //  CONFIGURE UI
    //-------------------------
    private fun configureToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configureBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_friend)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
    }

    private fun configureRecyclerView(){
        recyclerView = binding.addFriendViewRv
        adapterRv = UsersListAdapter(listOf(), viewModel)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterRv

    }

    private fun setupObserverViewModel(){
        setupSnackBar()
        setupUsersList()
    }

    private fun setupSnackBar(){
        val rooView = binding.addFriendsViewRoot
        rooView.setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

    }

    private fun configureEditTextListener(){
        binding.addFriendViewSearch.doAfterTextChanged { searchTerm ->
            val currentTextLength = searchTerm?.length
            Handler().postDelayed({
                if (currentTextLength == searchTerm?.length) viewModel.fetchUsers()
            }, 2000)
        }
    }

    private fun setupUsersList(){
        viewModel.usersLD.observe(this, Observer { updateUsersList(it) })
    }

    private fun updateUsersList(users: List<OtherUser>){
        adapterRv.update(users)
    }
}
