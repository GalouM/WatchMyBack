package com.galou.watchmyback.mainActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.data.repository.UserRepositoryImpl
import com.galou.watchmyback.utils.displayData
import com.google.firebase.auth.FirebaseUser

/**
 * [ViewModel] of the [MainActivity]
 *
 * Inherit from [ViewModel]
 *
 * @see ViewModel
 * @see MainActivity
 *
 *
 * @property userRepository [UserRepositoryImpl] reference
 */
class MainActivityViewModel(val userRepository: UserRepository) : ViewModel() {

    // -- Live data
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    private val _openSignInActivityEvent = MutableLiveData<Event<Unit>>()
    val openSignInActivityEvent: LiveData<Event<Unit>> = _openSignInActivityEvent

    val userLD: LiveData<User> = userRepository.currentUser

    //-----------------------
    // AUTHENTIFICATION
    //-----------------------

    /**
     * Check if a user is connected to the application
     *
     * If no user is connected, the viewModel request to open a sign in activity
     * Otherwise it fetches the user's information
     *
     * @see fetchCurrentUserInformation
     *
     * @param firebaseUser user connected to the app through Firebase Authentification
     */
    fun checkIfUserIsConnected(firebaseUser: FirebaseUser?){
        if(firebaseUser != null){
            fetchCurrentUserInformation(firebaseUser)
        } else{
            showSignInActivity()
        }

    }

    /**
     * Handle the information after the user has sig in
     * It either connect the user to the application or show the potential error
     *
     * @see checkIfUserIsConnected
     *
     * @param resultCode result of the connection
     * @param data data from the connection
     * @param firebaseUser user connected to the app through Firebase Authentification
     */
    fun handleSignIngActivityResult(resultCode: Int, data: Intent?, firebaseUser: FirebaseUser?){
        if(resultCode == Activity.RESULT_OK){
            checkIfUserIsConnected(firebaseUser)
        }
        else {
            val response = IdpResponse.fromResultIntent(data)
            when(response?.error?.errorCode){
                ErrorCodes.NO_NETWORK -> showSnackBarMessage(R.string.no_internet)
                ErrorCodes.UNKNOWN_ERROR -> showSnackBarMessage(R.string.unknown_error)
                null -> showSnackBarMessage(R.string.authentification_cancelled)
            }
            showSignInActivity()
        }

    }

    /**
     * Log out user from the app
     *
     * @param context app's [Context]
     */
    fun logOutUser(context: Context){
        AuthUI.getInstance().signOut(context)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    showSignInActivity()
                } else {
                    showSnackBarMessage(R.string.failed_signout)
                }
                
            }
    }

    /**
     * Fetch the users information from the remote database
     * Create a user in the remote database if it doesn't already exist
     *
     * @see createUserToRemoteDB
     * @see UserRepositoryImpl.getUserFromRemoteDB
     *
     * @param firebaseUser user connected to the app through Firebase Authentification
     */
    private fun fetchCurrentUserInformation(firebaseUser: FirebaseUser){
        userRepository.getUserFromRemoteDB(firebaseUser.uid)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val user = task.result
                    if(user != null) {
                        setupUserInformation(user)
                    } else {
                        createUserToRemoteDB(firebaseUser)
                    }

                } else {
                    showSnackBarMessage(R.string.error_fetching)
                }
            }
    }

    /**
     * Create a user in the remote databse
     *
     * @see UserRepositoryImpl.createUserInRemoteDB
     *
     * @param firebaseUser user connected to the app through Firebase Authentification
     */
    private fun createUserToRemoteDB(firebaseUser: FirebaseUser){
        val urlPhoto = firebaseUser.photoUrl?.toString()
        val user = User(firebaseUser.uid, firebaseUser.email, firebaseUser.displayName, firebaseUser.phoneNumber, urlPhoto)
        userRepository.createUserInRemoteDB(user).addOnCompleteListener { task -> 
            if(task.isSuccessful){
                fetchCurrentUserInformation(firebaseUser)
            } else {
                showSnackBarMessage(R.string.error_creatng_user_remote)
            }
        }
    }

    /**
     * Emit LiveData with the [User] information
     *
     * @param user [User] fetched from the database
     */
    private fun setupUserInformation(user: User){
        userRepository.currentUser.value = user
        showSnackBarMessage(R.string.welcome)
    }

    
    
    // UTILS
    private fun showSnackBarMessage(message: Int){
        _snackbarText.value = Event(message)
    }

    private fun showSignInActivity(){
        _openSignInActivityEvent.value = Event(Unit)
    }


}