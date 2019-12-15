package com.galou.watchmyback.utils.rvAdapter

import com.galou.watchmyback.data.applicationUse.Watcher

/**
 * @author galou
 * 2019-12-03
 */
interface WatcherListener {

    fun onClickWatcher(watcher: Watcher)
    fun onClickAddFriends()
}