package com.galou.watchmyback.main

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.base.BaseViewModel
import com.galou.watchmyback.data.entity.Trip
import com.galou.watchmyback.data.entity.TripUpdateFrequency
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.entity.UserWithPreferences
import com.galou.watchmyback.data.repository.*
import com.galou.watchmyback.utils.RESULT_ACCOUNT_DELETED
import com.galou.watchmyback.utils.Result
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

/**
 * [ViewModel] of the [MainActivity]
 *
 * Inherit from [BaseViewModel]
 *
 * @see MainActivity
 *
 *
 * @property userRepository [UserRepositoryImpl] reference
 */
class MainActivityViewModel(
    private val userRepository: UserRepository,
    private val checkListRepository: CheckListRepository,
    private val tripRepository: TripRepository,
    private val friendRepository: FriendRepository
) : BaseViewModel() {

    // -- Live data

    private val _openSignInActivityEvent = MutableLiveData<Event<Unit>>()
    val openSignInActivityEvent: LiveData<Event<Unit>> = _openSignInActivityEvent

    private val _openMyTripActivityLD = MutableLiveData<Event<Unit>>()
    val openMyTripActivityLD: LiveData<Event<Unit>> = _openMyTripActivityLD

    private val _enableLateNotificationLD = MutableLiveData<Event<String>>()
    val enableLateNotificationLD: LiveData<Event<String>> = _enableLateNotificationLD

    private val _enableBackHomeNotificationLD = MutableLiveData<Event<String>>()
    val enableBackHomeNotificationLD: LiveData<Event<String>> = _enableBackHomeNotificationLD

    private val _configureTripCheckUpLD = MutableLiveData<Event<Trip>>()
    val configureTripCheckUpLD: LiveData<Event<Trip>> = _configureTripCheckUpLD

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
        if (firebaseUser != null) {
            if (userLD.value == null) fetchCurrentUserInformation(firebaseUser)

        } else {
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
                    userRepository.currentUser.value = null
                    showSignInActivity()
                } else {
                    showSnackBarMessage(R.string.failed_signout)
                }
                
            }
    }

    //-----------------------
    // ACTIVITY RESULTS
    //-----------------------

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
        if(resultCode == RESULT_OK){
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
     * Show a message when the [User]'s information were updated
     *
     * @param resultCode
     */
    fun handleResultAfterProfileActivityClosed(resultCode: Int){
        if(resultCode == RESULT_OK) showSnackBarMessage(R.string.info_updated)
    }

    /**
     * Show the sign in activity if the [User] was deleted
     *
     * @param resultCode
     */
    fun handleResultSettingsActivity(resultCode: Int){
        if (resultCode == RESULT_ACCOUNT_DELETED){
            userRepository.currentUser.value = null
            showSignInActivity()
        }
    }

    //-----------------------
    // CREATE / FETCH / EMIT USER INFOS
    //-----------------------

    /**
     * Fetch the users information from the database
     *
     * @see createUserToDB
     * @see UserRepositoryImpl.fetchUser
     *
     * @param firebaseUser user connected to the app through Firebase Authentification
     */
    private fun fetchCurrentUserInformation(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            when (val result = userRepository.fetchUser(firebaseUser.uid)) {
                is Result.Success -> {
                    val user = result.data
                    if(user != null){
                        fetchFriends(user)
                    } else {
                        createUserToDB(firebaseUser)
                    }

                }
                is Result.Error -> {
                    showSnackBarMessage(R.string.error_fetching)
                    showSignInActivity()
                }
                is Result.Canceled -> {
                    showSnackBarMessage(R.string.canceled)
                    showSignInActivity()
                }

            }
        }
    }

    /**
     * Create a user in the  database
     *
     * @see UserRepositoryImpl.createNewUser
     *
     * @param firebaseUser user connected to the app through Firebase Authentification
     */
    private fun createUserToDB(firebaseUser: FirebaseUser){
        val urlPhoto = firebaseUser.photoUrl?.toString()
        val user = User(firebaseUser.uid, firebaseUser.email, firebaseUser.displayName, firebaseUser.phoneNumber, urlPhoto)
        viewModelScope.launch {
            when(userRepository.createNewUser(user)){
                is Result.Success -> fetchCurrentUserInformation(firebaseUser)
                is Result.Error -> showSnackBarMessage(R.string.error_creatng_user_remote)
                is Result.Canceled -> showSnackBarMessage(R.string.canceled)
            }
        }
    }

    /**
     * Emit LiveData with the [User] information
     *
     * @param user [User] fetched from the database
     */
    private fun setupUserInformation(user: UserWithPreferences){
        with(user.preferences!!){
            if (notificationBackSafe) _enableBackHomeNotificationLD.value = Event(user.user.id)
            if (notificationLate) _enableLateNotificationLD.value = Event(user.user.id)
        }
        userRepository.currentUser.value = user.user
        userRepository.userPreferences.value = user.preferences
        showSnackBarMessage(R.string.welcome)
    }

    //-----------------------
    // CREATE / FETCH CHECKLIST
    //-----------------------

    /**
     * Fetch all the check list of the current user
     *
     * @param userId Id of the user
     *
     * @see CheckListRepository.fetchUserCheckLists
     */
    private fun fetchCheckLists(user: UserWithPreferences){
        viewModelScope.launch {
            when(checkListRepository.fetchUserCheckLists(user.user.id, true)){
                is Result.Success -> fetchActiveTrip(user)
                is Result.Canceled, is Result.Error -> showSnackBarMessage(R.string.error_fetch_check_lists)
            }
        }

    }

    //-----------------------
    // CREATE / FETCH  ACTIVE TRIP
    //-----------------------

    /**
     * Fetch the active trip of the current user
     *
     * @see TripRepository.fetchUserActiveTrip
     *
     */
    private fun fetchActiveTrip(user: UserWithPreferences){
        viewModelScope.launch {
            when(val trip = tripRepository.fetchUserActiveTrip(user.user.id, true)){
                is Result.Success -> {
                    trip.data?.let {
                        if (trip.data.trip.updateFrequency != TripUpdateFrequency.NEVER){
                            _configureTripCheckUpLD.value = Event(trip.data.trip)
                        }
                        }
                    setupUserInformation(user)
                }
                else -> showSnackBarMessage(R.string.error_fetch_trip)
            }
            _dataLoading.value = false
        }

    }


    //-----------------------
    // CREATE / FETCH / FRIENDS
    //-----------------------

    /**
     * Fetch all the friends of the current user
     *
     * @see setupUserInformation
     * @see FriendRepository.fetchUserFriend
     */
    private fun fetchFriends(user: UserWithPreferences){
        viewModelScope.launch {
            when(friendRepository.fetchUserFriend(user.user, true)) {
                is Result.Success -> fetchCheckLists(user)
                else -> showSnackBarMessage(R.string.error_fetch_friends)
            }
        }
    }


    //-----------------------
    // OPEN ACTIVITIES
    //-----------------------

    /**
     * Emit the [Event] to show the sign in activity
     *
     */
    private fun showSignInActivity(){
        _openSignInActivityEvent.value = Event(Unit)
    }

    /**
     * TODOEmit the [Event] to hsow My Trip Activity
     * if the user has an active trip
     *
     */
    fun showMyTripActivity(){

        fun openActivity(){
            _openMyTripActivityLD.value = Event(Unit)
        }

        fun checkIsUserHasActiveTrip(){
            viewModelScope.launch {
                when(val result = tripRepository.fetchUserActiveTrip(userLD.value!!.id, false)){
                    is Result.Success -> {
                        if (result.data != null) openActivity()
                        else showSnackBarMessage(R.string.no_current_active_trip)
                    }
                    else -> showSnackBarMessage(R.string.error_fetch_trip)
                }
            }
        }

        checkIsUserHasActiveTrip()

    }




}