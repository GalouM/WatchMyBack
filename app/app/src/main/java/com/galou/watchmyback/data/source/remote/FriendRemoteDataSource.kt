package com.galou.watchmyback.data.source.remote

import com.galou.watchmyback.data.entity.Friend
import com.galou.watchmyback.data.entity.User
import com.galou.watchmyback.data.source.FriendDataSource
import com.galou.watchmyback.utils.FRIEND_COLLECTION_NAME
import com.galou.watchmyback.utils.Result
import com.galou.watchmyback.utils.await
import com.galou.watchmyback.utils.extension.toUserList
import com.galou.watchmyback.utils.idGenerated
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author galou
 * 2019-11-04
 */
class FriendRemoteDataSource : FriendDataSource {

    private val ioDispatcher = Dispatchers.IO

    private val remoteDB = FirebaseFirestore.getInstance()
    private val friendCollection = remoteDB.collection(FRIEND_COLLECTION_NAME)

    override suspend fun addFriend(friend: Friend): Result<Void?> = withContext(ioDispatcher)  {
        return@withContext try {
            friendCollection.document(idGenerated).set(friend).await()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    override suspend fun removeFriend(userId: String, friendId: String): Result<Void?> = withContext(ioDispatcher) {
        return@withContext try {
            when(val idResult = getFriendId(userId, friendId)){
                is Result.Success -> {
                    friendCollection.document(idResult.data).delete().await()
                }
                is Result.Error -> Result.Error(idResult.exception)
                is Result.Canceled -> Result.Canceled(idResult.exception)
            }

        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    private suspend fun getFriendId(userId: String, friendId: String): Result<String> = withContext(ioDispatcher) {
        return@withContext try {
            when(
                val resultQuery =
                    friendCollection.whereEqualTo("userId", userId)
                        .whereEqualTo("friendId", friendId)
                        .get()
                        .await()){
                is Result.Success -> {
                   Result.Success(resultQuery.data.documents[0].id)

                }
                is Result.Error -> Result.Error(resultQuery.exception)
                is Result.Canceled -> Result.Canceled(resultQuery.exception)
            }

        } catch (e: Exception) {
            Result.Error(e)
        }

    }

    override suspend fun fetchUserFriend(userId: String): Result<List<User>> = withContext(ioDispatcher) {
        return@withContext try {
            when(
                val resultDocumentSnapshot =
                    friendCollection.whereEqualTo("userId", userId).get().await()
                ){
                is Result.Success -> {
                    val friends = resultDocumentSnapshot.data.toUserList()
                    Result.Success(friends)
                }
                is Result.Error -> Result.Error(resultDocumentSnapshot.exception)
                is Result.Canceled -> Result.Canceled(resultDocumentSnapshot.exception)
            }

        } catch (e: Exception) {
            Result.Error(e)
        }

    }

}