package com.galou.watchmyback.utils.extension

import com.galou.watchmyback.data.entity.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

/**
 * @author galou
 * 2019-11-04
 */

/**
 * Convert a Query Snapshot into a list of [User]
 *
 * @return a list of user
 */
fun QuerySnapshot.toUserList(): List<User> = this.toObjects(User::class.java)

/**
 * Convert a Document Snapshot into a User
 *
 * @return a [User]
 */
fun DocumentSnapshot.toUser(): User? = this.toObject(User::class.java)
