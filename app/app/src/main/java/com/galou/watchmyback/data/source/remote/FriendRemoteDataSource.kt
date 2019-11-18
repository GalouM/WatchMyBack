package com.galou.watchmyback.data.source.remote

import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.FriendDataSource
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.USER_COLLECTION_NAME
import com.galou.watchmyback.utils.await
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

/**
 * Implementation of [FriendDataSource] for the remote database
 *
 * List all the possible actions on the remote database for the friends
 *
 *
 * @param remoteDB Reference toward the remote database
 */
class FriendRemoteDataSource(
    remoteDB: FirebaseFirestore
) : FriendDataSource {

    private val ioDispatcher = Dispatchers.IO
    private val userCollection = remoteDB.collection(USER_COLLECTION_NAME)

    /**
     * Add a friend to the specified user in the database
     *
     * @param user [User] who onw the friend
     * @param friend [User] who is the friend
     * @return [Result] of the operation
     */
    override suspend fun addFriend(user: User, vararg friend: User): Result<Void?> = withContext(ioDispatcher)  {
        user.friendsId.addAll(friend.map { it.id })
        return@withContext userCollection.document(user.id).update("friendsId", user.friendsId).await()
    }

    /**
     * Remove a [User]'s friend in the database
     *
     * @param user [User] who own the friend
     * @param friendId ID of the friend
     * @return [Result] of the deletion
     */
    override suspend fun removeFriend(user: User, friendId: String): Result<Void?> = withContext(ioDispatcher) {
        user.friendsId.remove(friendId)
        return@withContext userCollection.document(user.id).update("friendsId", user.friendsId).await()

    }

    /**
     * Fetch all the [User]'s friend from the database
     *
     * @param user Look for the friends of this [User]
     * @return [Result] containing a list of [User]
     */
    override suspend fun fetchUserFriend(user: User): Result<List<User>> = withContext(ioDispatcher) {
        val friends = mutableListOf<User>()
        supervisorScope {
            for (friendId: String in user.friendsId){
                launch {
                    when (val friendResult = userCollection.document(friendId).get().await()) {
                        is Result.Success -> friendResult.data.toObject(User::class.java)?.let { friend ->
                            friends.add(friend)
                        }

                    }
                }
            }
            return@supervisorScope Result.Success(friends)
        }
    }

}