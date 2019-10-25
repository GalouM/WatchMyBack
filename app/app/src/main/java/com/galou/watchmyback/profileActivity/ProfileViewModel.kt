package com.galou.watchmyback.profileActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.galou.watchmyback.Event
import com.galou.watchmyback.R
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.extension.isCorrectEmail
import com.galou.watchmyback.utils.extension.isCorrectName
import com.galou.watchmyback.utils.extension.isCorrectPhoneNumber

/**
 * Created by galou on 2019-10-25
 */
class ProfileViewModel (val userRepository: UserRepository){

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    private val _usernameLD = MutableLiveData<String>()
    val usernameLD: LiveData<String> = _usernameLD

    private val _emailLD = MutableLiveData<String>()
    val emailLD: LiveData<String> = _emailLD

    private val _pictureUrlLD = MutableLiveData<String>()
    val pictureUrlLD: LiveData<String> = _pictureUrlLD

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _errorEmail = MutableLiveData<Int?>()
    val errorEmail: LiveData<Int?> = _errorEmail

    private val _errorUsername = MutableLiveData<Int?>()
    val errorUsername: LiveData<Int?> = _errorUsername

    private val _errorPhoneNumber = MutableLiveData<Int?>()
    val errorPhoneNumber: LiveData<Int?> = _errorPhoneNumber
    
    private var user: User

    init {
        _dataLoading.value = true
        
        user = userRepository.currentUser!!
        _usernameLD.value = user.username
        _emailLD.value = user.email
        _pictureUrlLD.value = user.pictureUrl
        _phoneNumber.value = user.phoneNumber
        
        _dataLoading.value = false
    }

    fun updateUserInformation(){
        _dataLoading.value = true
        _errorEmail.value = null
        _errorPhoneNumber.value = null
        _errorUsername.value = null
        if (userInputCorrect()){
            updateUserInDB()
        }


    }

    private fun updateUserInDB(){
        userRepository.updateUserInRemoteDB(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    userRepository.currentUser = user
                    showSnackBarMessage(R.string.info_updated)
                } else {
                    showSnackBarMessage(R.string.fail_not_saved)
                }
            }
    }

    private fun userInputCorrect(): Boolean {
        var infoCorrect = true
        if (_usernameLD.value?.isCorrectName() == false){
            _errorUsername.value = R.string.incorrect_username
            infoCorrect = false
        }
        if (_emailLD.value?.isCorrectEmail() == false){
            _errorEmail.value = R.string.incorrect_email
            infoCorrect = false
        }
        if (_phoneNumber.value?.isCorrectPhoneNumber() == false) {
            _errorPhoneNumber.value = R.string.incorrect_phone_number
            infoCorrect = false
        }

        return infoCorrect

    }

    // UTILS
    private fun showSnackBarMessage(message: Int){
        _snackbarText.value = Event(message)
    }
}