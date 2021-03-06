package com.galou.watchmyback.addFriend

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galou.watchmyback.R
import com.galou.watchmyback.data.applicationUse.OtherUser
import com.galou.watchmyback.databinding.ActivityAddFriendBinding
import com.galou.watchmyback.utils.extension.setupSnackBar
import com.galou.watchmyback.utils.extension.visibleOrInvisible
import com.galou.watchmyback.utils.rvAdapter.UsersListAdapter
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun configureBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_friend)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
    }

    private fun configureRecyclerView(){
        recyclerView = binding.addFriendViewRv
        adapterRv = UsersListAdapter(
            listOf(),
            viewModel
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterRv

    }

    private fun setupObserverViewModel(){
        setupSnackBar()
        setupUsersList()
    }

    private fun setupSnackBar(){
        binding.addFriendsViewRoot
            .setupSnackBar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)
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
        binding.addFriendsFriendsViewNoUser.visibleOrInvisible(users.isEmpty())

    }
}
