package com.galou.watchmyback.addFriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galou.watchmyback.R
import com.galou.watchmyback.addFriend.FetchType.EMAIL_ADDRESS
import com.galou.watchmyback.addFriend.FetchType.USERNAME
import com.galou.watchmyback.base.UserListBaseViewModel
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.repository.FriendRepository
import com.galou.watchmyback.data.repository.UserRepository
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.extension.toListOtherUser
import kotlinx.coroutines.launch

/**
 * [ViewModel] of the [AddFriendActivity]
 *
 * Inherit from [UserListBaseViewModel]
 *
 * @property userRepository [UserRepositoryImpl] reference
 * @property friendRepository [FriendRepositoryImpl] reference
 */
class AddFriendViewModel(
    private val userRepository: UserRepository,
    private val friendRepository: FriendRepository
) : UserListBaseViewModel(userRepository, friendRepository) {


    private val _fetchType = MutableLiveData<FetchType>(USERNAME)
    val fetchType: LiveData<FetchType> = _fetchType

    val searchPattern = MutableLiveData<String>("")

    init {
        fetchUsers()
    }

    /**
     * Determine which type of search should be executed
     *
     * @see fetchUserByEmailAddress
     * @see fetchAllUsers
     * @see fetchUserByUsername
     *
     */
    fun fetchUsers(){
        when {
            searchPattern.value!!.isBlank() -> fetchAllUsers()
            _fetchType.value == USERNAME -> fetchUserByUsername()
            _fetchType.value == EMAIL_ADDRESS -> fetchUserByEmailAddress()
            else -> fetchAllUsers()
        }

    }

    /**
     * Set the [FetchType] depending on the user selection
     *
     * @param fetchType
     *
     * @see fetchUsers
     */
    fun changeTypeSearch(fetchType: FetchType){
        _fetchType.value = fetchType
        fetchUsers()
    }

    /**
     * Fetch all the users from the database
     *
     * @see fetchResultUsers
     * @see UserRepository.fetchAllUsers
     *
     */
    private fun fetchAllUsers() {
        _dataLoading.value = true
        viewModelScope.launch { fetchResultUsers(userRepository.fetchAllUsers()) }
    }

    /**
     * Fetch all users who have a specific username
     *
     * @see fetchResultUsers
     * @see UserRepository.fetchUserByUsername
     *
     */
    private fun fetchUserByUsername() {
        _dataLoading.value = true
        _fetchType.value = USERNAME
        viewModelScope.launch { fetchResultUsers(userRepository.fetchUserByUsername(searchPattern.value!!)) }
    }

    /**
     * Fetch all users who have a specific email address
     *
     * @see fetchResultUsers
     * @see UserRepository.fetchUserByEmailAddress
     *
     */
    private fun fetchUserByEmailAddress() {
        _dataLoading.value = true
        _fetchType.value = EMAIL_ADDRESS
        viewModelScope.launch { fetchResultUsers(userRepository.fetchUserByEmailAddress(searchPattern.value!!)) }
    }

    /**
     * Determine if an operation was successful and either display its data or show an error
     *
     * @param result [Result] of the operation
     *
     * @see showUsersList
     * @see showSnackBarMessage
     */
    private fun fetchResultUsers(result: Result<List<User>>){
        when (result) {
            is Result.Success -> {
                showUsersList(result.data.toListOtherUser(false))
                if (result.data.isEmpty()) showSnackBarMessage(R.string.no_user)

            }
            is Result.Error -> showSnackBarMessage(R.string.no_user)
            is Result.Canceled -> showSnackBarMessage(R.string.canceled)
        }
    }


}

/**
 * Different type to look for users
 *
 * @property USERNAME search by username of the users
 * @property EMAIL_ADDRESS search by email address of the users
 *
 */
enum class FetchType {
    USERNAME,
    EMAIL_ADDRESS
}