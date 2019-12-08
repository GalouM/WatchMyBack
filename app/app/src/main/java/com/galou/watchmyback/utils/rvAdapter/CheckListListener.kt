package com.galou.watchmyback.utils.rvAdapter

import com.galou.watchmyback.data.entity.CheckListWithItems

/**
 * @author galou
 * 2019-12-03
 */
interface CheckListListener {
    fun onClickCheckList(checkList: CheckListWithItems)
    fun onClickCreateCheckList()
}