package com.galou.watchmyback.data.source.remote

import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.FriendDataSource
import com.galou.watchmyback.utils.*
import com.galou.watchmyback.utils.extension.toUserList
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

/**
 * @author galou
 * 2019-11-04
 */
class FriendRemoteDataSource(
    remoteDB: FirebaseFirestore
) : FriendDataSource {

    private val ioDispatcher = Dispatchers.IO
    private val userCollection = remoteDB.collection(USER_COLLECTION_NAME)

    override suspend fun addFriend(user: User, vararg friend: User): Result<Void?> = withContext(ioDispatcher)  {
        user.friendsId.addAll(friend.map { it.id })
        return@withContext userCollection.document(user.id).update("friendId", user.friendsId).await()
    }

    override suspend fun removeFriend(user: User, friendId: String): Result<Void?> = withContext(ioDispatcher) {
        user.friendsId.remove(friendId)
        return@withContext userCollection.document(user.id).update("friendId", user.friendsId).await()

    }

    override suspend fun fetchUserFriend(user: User): Result<List<User>> = withContext(ioDispatcher) {
        val friends = mutableListOf<User>()
        supervisorScope {
            for (friendId: String in user.friendsId){
                launch {
                    val friendResult = userCollection.document(friendId).get().await()
                    if (friendResult is Result.Success){
                        friendResult.data.toObject(User::class.java)?.let { friend ->
                            friends.add(friend)
                        }
                    }
                }
            }
            return@supervisorScope Result.Success(friends)
        }
    }

}