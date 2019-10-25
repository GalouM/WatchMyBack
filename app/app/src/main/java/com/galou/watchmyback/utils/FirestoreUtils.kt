package com.galou.watchmyback.utils

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import java.lang.IllegalStateException

/**
 * https://code.luasoftware.com/tutorials/google-cloud-firestore/firestore-query-addsnapshotlistener-as-livedata/
 */
class QuerySnapshotLiveData(private val query: Query) : LiveData<Resource<QuerySnapshot>>(),
        EventListener<QuerySnapshot> {

    private  var registration: ListenerRegistration? = null

    override fun onEvent(snapShots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        value = if (e != null){
            Resource(e)
        } else {
            Resource(snapShots!!)
        }

    }

    override fun onActive() {
        super.onActive()
        registration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        registration?.also {
            it.remove()
            registration = null
        }
    }
}

class Resource<T> private constructor(
    private val data: T?,
    private val error: Exception?
){
    val isSuccessful: Boolean
        get() = data != null && error != null

    constructor(data: T) : this(data, null)
    constructor(exception: Exception) : this(null, exception)

    fun data() : T {
        check(error == null) { "Check isSuccessful first: call error() instead." }
        return data!!
    }

    fun error(): Exception{
        check(data == null) { "Check isSuccessful first: call data() instead." }
        return error!!
    }
}